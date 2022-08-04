package man.ui

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import groovy.transform.CompileStatic

@CompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack" , "true");
        GrailsApp.run(Application, args)
    }
}