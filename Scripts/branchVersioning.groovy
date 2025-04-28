def branches = ["release/1.1315.0", "release/1.26.1", "release/1.0.2", "release/1.150.3", "release/1.9.0", "release/1.1.1", "release/1.1.2"]

def versions = []

for (branch in branches){
    def version = branch.replaceAll("release/", "").split("\\.")
    def numeric = (version[0].toInteger()*1000000)+(version[1].toInteger()*1000) + (version[2].toInteger())
    versions << numeric
}

println(versions.sort().reverse())
println(versions.sort().reverse()[0])
println(versions.sort().reverse()[1])

if(branches.size() > 1){

    def prevNum = versions.sort().reverse()[1]

    def prevBranch = branches.find { branch ->
        def versionParts = branch.replace("release/", "").split("\\.")
        println("versionParts - ${versionParts}")
        def numeric = (versionParts[0].toInteger()*1000000)+(versionParts[1].toInteger()*1000) + (versionParts[2].toInteger())

        return numeric == prevNum
    }

    if(prevBranch){
        println(prevBranch)
        // env.PREV_RELEASE_BRANCH=prevBranch
    }
}

