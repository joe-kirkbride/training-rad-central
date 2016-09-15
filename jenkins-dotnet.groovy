// jenkins pipeline :: dotnet

def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def slackChannel = "#<channel>"
def slackColorFail = "#AA0000"
def slackColorPass = "#00AA00"
def slackMessageFail = "FAIL"
def slackMessagePass = "PASS"
def toolCurl = "<path-to-curl>"
def toolMsBuild = "<path-to-msbuild>"
def toolMsDeploy = "<path-to-msdeploy>"
def toolNuget = "<path-to-nuget>"
def toolSonarQube = "<path-to-sonarqube>"

def buildProcess(extension, target) {
  dir.eachFileRecurse {
    file ->
      if (file.name.indexOf("${extension}") != -1) {
        bat "${toolNuget} restore"
        bat "${toolMsBuild} ${file.name} ${target}"
      }
  }
}

def slackNotify(slackChannel, slackColor, slackMessage) {
  def author = bat returnStdout: true, script: "git log -1 --pretty=%%an"
  def branch = bat returnStdout: true, script: "git rev-parse --abbrev-ref=strict head"
  def commit = bat returnStdout: true, script: "git log -1 --pretty=\"%%s - %%h\""
  
  author = (author.split("\n"))[2]
  branch = (branch.split("\n"))[2]
  commit = (commit.split("\n"))[2]
  message = "${author} - ${branch} - ${commit}"
  
  slackSend channel: slackChannel, color: slackColor, message: "${slackMessage}\n${message}"
}

stage("import") {
  node() {
    try {
      git branch: gitBranch, url: gitUrl
      slackNotify(slackChannel, slackColorPass, slackMessagePass)
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("analyze") {
  node() {
    try {
      buildProcess(".sln", "/t:clean;build")
      buildProcess(".csproj", "/t:package")
      slackNotify(slackChannel, slackColorPass, slackMessagePass)
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("test") {
  node() {
    try {
      slackNotify(slackChannel, slackColorPass, slackMessagePass)
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("deploy") {
  node() {
    try {
      slackNotify(slackChannel, slackColorPass, slackMessagePass)
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}

stage("export") {
  node() {
    try {
      slackNotify(slackChannel, slackColorPass, slackMessagePass)
    } catch(error) {
      slackSend channel: slackChannel, color: slackColorFail, message: slackMessageFail
      throw error
    }
  }
}
