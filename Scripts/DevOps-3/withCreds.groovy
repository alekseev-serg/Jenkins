node(agent: "builder"){
    stage('take a cred'){
        withCredentials([string(credentialsId: 'my-token', variable: 'TOKEN')]){
            sh 'curl -H "Authorization: Bearer $TOKEN" https://api.example.com'
        }
    }
}