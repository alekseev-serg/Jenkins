def branches = ["kek/1.1315.0", "    release/1.1315.0", "   release/1.26.1", "   release/1.0.2", "   release/1.150.3", "   release/1.9.0", "   release/1.1.1", "   release/1.1.2"]

def filtred = branches.findAll {it.trim().startsWith("release/")}

filtred.each { println it.trim() }
