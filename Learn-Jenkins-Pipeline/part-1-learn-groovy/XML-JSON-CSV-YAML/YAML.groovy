import groovy.yaml.YamlSlurper

def ys = new YamlSlurper()

def yaml = ys.parseText '''
language: groovy
sudo: required
dist: latest

matrix:
  include:
    - jdk: openjdk11
    - jdk: oraclejdk10
    - jdk: oraclejdk9

'''

println yaml.language             // groovy
println yaml.sudo                 // required
println yaml.dist                 // latest
println yaml.matrix.include.jdk   // [openjdk11, oraclejdk10, oraclejdk9]

// Создаём YAML
import groovy.yaml.YamlBuilder

def builder = new YamlBuilder()
builder.records {
    car {
        name 'Stuff'
        make 'RU'
        year 2026
        country 'Russia'
        homepage new URL('http://stepik.org')
        record {
            type '0003'
            description 'production 007'
        }
    }
}

println builder.toString()