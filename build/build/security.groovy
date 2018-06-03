#!groovy
import hudson.*
import hudson.security.*
import jenkins.model.*
import jenkins.security.s2m.AdminWhitelistRule
import jenkins.model.Jenkins

def instance = Jenkins.getInstance()

def user = "admin"
def pass = "jenkins"

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(user, pass)
instance.setSecurityRealm(hudsonRealm)

def strategy = new ProjectMatrixAuthorizationStrategy()
strategy.add(instance.ADMINISTER, user)
strategy.add(instance.READ, "authenticated")
instance.setAuthorizationStrategy(strategy)

instance.save()
Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
