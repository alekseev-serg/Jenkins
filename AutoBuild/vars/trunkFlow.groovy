def call(ctx){
    def version = defineVersion(ctx);

    if (ctx.event == "merge"){
        if (version == null) {
            // Не можем обработать
            echo "Не можем обработать версию"
            return;
        }
        return callDpm(ctx.appName, version, ctx.branch)
    }

    if (ctx.event == "push" && ctx.branch.contains("release/")){
        if (version == null) {
            // Не можем обработать
            echo "Не можем обработать версию"
            return;
        }
        return callDpm(ctx.appName, version, ctx.branch)
    }

    return callJob(ctx.appName, ctx.gitUrl, ctx.branch)
}

def defineVersion(ctx) {
    def lastTag = sh(
        script: "git tag --merged ${ctx.commitHash} --sort=-creatordate | grep '^v[0-9]\\+\\.[0-9]\\+\\.[0-9]\\+\$' | head -n 1", 
        returnStdout: true
    ).trim();

    echo "Последний тег ${lastTag ?: 'не определён'}";
    
    // Если тегов нет
    if (lastTag == "") {
        if (ctx.branch.contains('release/')) {
            def rawVersion = ctx.branch.replace('release/', '')
            def meta = parseVersion(rawVersion);
            
            return sanitizeAndSyncVersion(ctx.branch, "${meta.major}.${meta.minor}.${meta.patch ?: 0}", true);
        }

        if (ctx.branch == 'master') {
            return sanitizeAndSyncVersion(ctx.branch, "0.0.1", false, true);
        }

        return;
    }
    
    def tagCommit = sh(script: "git rev-list -n 1 ${lastTag}", returnStdout: true).trim();

    def (major, minor, patch) = lastTag.replaceFirst("v", "").tokenize("\\.");

    def lastVersion = parseVersion(lastTag);

    if (ctx.branch.contains('release/')) {
        def rawVersion = ctx.branch.replace('release/', '');
        def currentVersion = parseVersion(rawVersion);

        echo "Обрабатываем релизную ветку ${rawVersion}";
        //Актуальный тег уже был выпущен раннее
        if("${currentVersion.major}.${currentVersion.minor}" == "${lastVersion.major}.${lastVersion.minor}"){
            echo "Обрабатываем релизную ветку ${rawVersion}, комиты последнего тега ${ctx.commitHash == tagCommit ? 'не отличаются' : 'отличаются'}";
            // Если привязан, то не выпускаем новый тег, а запускаем сборку с текущей версией
            if(ctx.commitHash == tagCommit) {
                return sanitizeAndSyncVersion(ctx.branch, "${lastVersion.major}.${lastVersion.minor}.${lastVersion.patch}");
            }
            // Если не привязана, то мы должны выпустить новый тег к последнему коммиту
            // Увеличиваем патч
            return sanitizeAndSyncVersion(ctx.branch, "${currentVersion.major}.${currentVersion.minor}.${currentVersion.patch} + 1", true);
        }
        // Нужно создать тег
        return sanitizeAndSyncVersion(ctx.branch, "${currentVersion.major}.${currentVersion.minor}.0", true);
    }
    if (ctx.branch == 'master'){
        return sanitizeAndSyncVersion(ctx.branch, "${lastVersion.major}.${lastVersion.minor + 1}.0", false, true);
    }
    return;
}

def sanitizeAndSyncVersion(String branch, String version, boolean shouldSyncRemote = false, boolean isAlpha = false) {
    def gitTag = isAlpha ? "${verson}-alpha.${currentBuild.number}" : "${version}";

    echo "Определена версия ${gitTag}";

    if (shouldSyncRemote) {
        sh "git checkout ${branch}"
        sh "git tag v${gitTag} ${branch}";

        sshagent(credentials: [env.GIT_CREDS ?: scm.userRemoteConfigs?.get(0)?.credentialsId]) {
            sh "git push origin tag v${gitTag}";
        }
    }

    return gitTag;
}

def parseVersion(rawVersion){
    def (version, prefix) = rawVersion.replaceFirst("v", "").tokenize("-");

    def (major, minor, patch) = version.tokenize("\\.");
    def (slug, buildVersion) = (prefix ?: "").tokenize("\\.");

    return [
        major: major.toInteger(),
        minor: minor.toInteger(),
        patch: patch.toInteger(),
        slug: slug,
        buildVersion: buildVersion,
    ];
}

return this