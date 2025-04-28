// ----- 5 - FOR / WHILE ------

for (int i = 0; i < 5; i++) {
    print "This is i = ${i}"
}

for (i in 0..5) {
    print i
}

def numbers = [1, 2, 3, 4, 5]

for (number in numbers) {
   println "current number: $number"
}

def text = "QWERTY"
def list = []
for (ch in text) {
    list.add(ch)
}

for (Integer i : 0..5) {
    print i
}

def x1=0
while(x1++ < 5){
    print "x1: " + x1 + "\n"
}

def count = 5
def fact = 1
do {
    fact *= count--
} while(count > 1)
println fact // fact: 120