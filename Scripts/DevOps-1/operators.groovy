def a = 10
def b = 4
println "${a + b}"
println "${a - b}"
println "${a * b}"
println "${a / b}"

if (a > b) {
    println("A > B")
}else{
    println("A != B")
}

print "Enter a number 1 > "
def num = System.console().readLine().toInteger();

if(num%2 == 0){
    print "Чётное";
}else{
    print "Нечётное";
}