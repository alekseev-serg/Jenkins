// ----- 3 - IF ELSE example ------
def t1 = false
def t2 = true

if ( !t1 ) {
    println "t1 is $t1"
}

if ( t2 ) {
    println "t2 is ${t2}"
} else {
    println '...'
}

def b1 = false
def b2 = true

if ( b1 ) {
    println "b1: ${b1}"
} else if ( b2 ) {
    println "b2: ${b2} and b1: $b1"
} else if (b1 && b2) {
    println 'ALL is TRUE'
} else {
    println 'else....'
}


// условие ? значение1 : значение2
def num = 3
def condition = (num > 1) ? 'True' : 'False'

println(condition)