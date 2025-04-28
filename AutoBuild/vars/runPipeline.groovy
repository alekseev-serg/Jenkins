def call (){
    properties([
        parameters ([
            string(name: 'JSON_PAYLOAD', description: 'Generic body state'),
            string(name: 'JSON_QUERY', description: 'Generic query state'),
        ]),

        pipelineTriggers([[
            $class: 'GenericTrigger',
            genericVariables: [[key: 'JSON_PAYLOAD', value: '$']],
            genericRequestVariables: [[key: 'JSON_QUERY', value: '$']],
            causeString: 'PIPELINE',
            token: 'some-token-string',
            printContributedVariables: true,
            printPostContent: true
        ]])
    ]);

    // ENVIRONMENTS
    env.BUILD_JOB_NAME = env.BUILD_JOB_NAME ?: 'CI_4321';
    env.DEVOPS_CREDENTIALS_ID = env.DEVOPS_CREDENTIALS_ID ?: null;
    env.DEVOPS_HOST = env.DEVOPS_HOST ?: "https://host.ru";
    env.BB_CREDENTIALS = env.BB_CREDENTIALS ?: null;

    node("linux||Default||clearCI") {
        def webhookPayload = readJSON text: env.JSON_PAYLOAD;
        def webhookQuery = readJSON text: env.JSON_QUERY;

        def ctx = init(webhookPayload)

        def repositoryUri = new URI(ctx.gitUrl);

        dir('source') {
            sshagent(credentials: [env.GIT_CREDS ?: scm.userRemoteConfigs?.get(0)?.credentialsId]) {
                try {
                    sh "git clone ${ctx.gitUrl} ./"

                    if (webhookQuery.flow && webhookQuery.flow == "gf") {
                        gitFlow(ctx);
                    } else {
                        trunkFlow(ctx);
                    }
                } catch (Exception e) {
                    notifyBitbucket(
                        commitSha1: '${ctx.commitHash}',
                        considerUnstableAsSuccess: true,
                        credentialsId: env.BB_CREDENTIALS,
                        disableInprogressNotification: false,
                        ignoreUnverifieldSSLPeer: true,
                        includeBuildNumberInKey: false,
                        prependParentProjectKey: false,
                        buildStatus: "FAILURE",
                        buildName: "[${JOB_NAME.split("/").last().trim()}] ${e.message}",
                        projectKey: repositoryUri.path.tokenize("/")[0]
                        stashServerBaseUrl: "https://" + repositoryUri.host
                    )
                    echo e;
                    throw e;
                } finally {
                    cleanWs(disableDeferredWipeout: true, deleteDirs: true)
                }
            } 
        }
    }
}