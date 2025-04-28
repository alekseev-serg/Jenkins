import groovy.xml.XmlParser
import groovy.xml.XmlSlurper

def text = '''
    <list>
        <technology>
            <name>Groovy Lang</name>
        </technology>
    </list>
'''
// XmlSlurper
def list = new XmlSlurper().parseText(text)
println list.technology.name

// XmlParser
def l = new XmlParser().parseTex
t(text)
println l.technology.name.text()