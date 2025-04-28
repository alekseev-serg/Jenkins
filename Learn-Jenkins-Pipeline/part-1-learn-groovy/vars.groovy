def x = 10
def y = 'Hello World'
def z = 0.5
println(x * z)
println(y)
def b = true
println(b)

def map = [:]
map."name" = "John"
map."age" = 25

println(map)

println(y.getClass())

def abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

println(abc[0,25])
println(abc[0, ('ABCDEFGHIJKLMNOPQRSTUVWXYZ'.size() - 1)])
println(abc[0]+ abc[-1])