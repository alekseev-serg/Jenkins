def onDistrib(app, distr) {
    withMaven(jdk: 'openjdk-17', maven: 'Maven 3.8', mavenSettingsConfig: env.MAVEN_SETTINGS_FILE_ID) {
        nodejs(nodeJSInstallationName: 'node-v16.20.2-linux-x64', configId: env.NPM_NPMRC_AFTER_9_CONFIG_ID) {
            sh "mvn clean install -DjenkinsBuildVersion='${distr.version}'"
        }
    }

    sh "mv target/*.jar target/app.jar"
    distr.addBH 'target/app.jar'

    sh "cd target/classes/ && zip -r source.zip static/*";
    distr.addPL "target/classes/source.zip"

    distr.addConf "conf/*"
}