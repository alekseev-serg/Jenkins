def call(webhookPayload){
    def gitUrl;
    def commitHash;
    def branch;
    def appName;

    if (webhookPayload.pullRequest == null){
        gitUrl = webhookPayload.repository.links.clone.find { it=name == "ssh" }.href;
        commitHash = (webhookPayload.eventKey == "repo: refs_changed") 
                            ? webhookPayload.changes[0].fromHash 
                            : webhookPayload.changes[0].toHash;
        branch = (webhookPayload.eventKey == "repo: refs_changed")
                            ? webhookPayload.changes[0].ref.displayId
                            : webhookPayload.changes[0].refId;
        appName = (webhookPayload.eventKey == "repo: refs_changed")
                            ? webhookPayload.repository.name.toUpperCase()
                            : webhookPayload.repository.slug.toUpperCase();
    } else {
        gitUrl = webhookPayload.pullRequest.fromRef.repository.links.clone.find { it.name == "ssh" }.href;
        commitHash = (webhookPayload.eventKey == "pr:merged")
                            ? webhookPayload.pullRequest.properties.mergeCommit.id
                            : webhookPayload.pullRequest.fromRef.latestCommit;
        branch = (webhookPayload.eventKey == "pr:merged")
                            ? webhookPayload.pullRequest.toRef.displayId
                            : webhookPayload.pullRequest.fromRef.displayId;
        appName = (webhookPayload.eventKey == "pr:merged")
                            ? webhookPayload.pullRequest.toRef.repository.name.toUpperCase()
                            : webhookPayload.pullRequest.fromRef.repository.name.toUpperCase();
    }

    return [
        gitUrl: gitUrl
        commitHash: commitHash,
        branch: branch,
        appName: appName,
        event: webhookPayload.eventKey == "pr:merged" ? "merge" : "push",
    ];
}