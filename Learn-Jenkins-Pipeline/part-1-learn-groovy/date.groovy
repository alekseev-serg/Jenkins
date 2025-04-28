Date date = new Date()
println(date.toString())

Date date0 = new Date("06/10/1991")
println(date0.toString())

Date date1 = new Date("01/11/2024")
println("date1 = ${date1}")
println(date0.equals(date1))

println(date1.getTime())

date1.setTime(10000000)
println(date1)