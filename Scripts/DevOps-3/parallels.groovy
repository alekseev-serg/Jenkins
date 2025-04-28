node {
    parallel(
        serviceA: {
            stage('Build Python'){
                echo 'Build A'
            }
        },
        serviceB: {
            stage('Build React'){
                echo 'Build B'
            }
        }
    )
}