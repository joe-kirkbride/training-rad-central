// jenkins pipeline :: dotnet

def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def slackChannel = ""
def slackColorFail = ""
def slackColorPass = ""
def slackMessageFail = ""
def slackMessagePass = ""

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
