// jenkins pipeline :: dotnet

def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def slackChannel = "#<channel>"
def slackColorFail = "#AA0000"
def slackColorPass = "#00AA00"
def slackMessageFail = "FAILURE"
def slackMessagePass = "SUCCESS"
def toolCurl = "<path-to-curl>"
def toolMsBuild = "<path-to-msbuild>"
def toolMsDeploy = "<path-to-msdeploy>"
def toolSonarQube = "<path-to-sonarqube>"

stage("import") {
  node() {
    try {
      git branch: gitBranch, url: gitUrl

      slackSend channel: slackChannel, color: slackColorPass, message: slackMessagePass
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("analyze") {
  node() {
    try {
      slackSend channel: slackChannel, color: slackColorPass, message: slackMessagePass
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("test") {
  node() {
    try {
      slackSend channel: slackChannel, color: slackColorPass, message: slackMessagePass
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("deploy") {
  node() {
    try {
      slackSend channel: slackChannel, color: slackColorPass, message: slackMessagePass
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("export") {
  node() {
    try {
      slackSend channel: slackChannel, color: slackColorPass, message: slackMessagePass
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}
