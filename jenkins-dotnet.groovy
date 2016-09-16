// jenkins pipeline :: dotnet

def buildColor = ["#AA0000", "#00AA00"]
def buildFlag = ["FAIL", "PASS"]
def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def slackChannel = "#<channel>"
def toolCurl = "<path-to-curl>"
def toolMsDeploy = "<path-to-msdeploy>"
def toolSonarQube = "<path-to-sonarqube>"

def dotnetBuild(extension, target) {
  def files = findFiles glob: "**/${extension}"
  def path = ""
  def toolMsBuild = "<path-to-msbuild>"
  def toolNuget = "<path-to-nuget>"

  for (file in files.toList()) {
    path = file.path.replace(file.name, "")

    dir("${path}") {
      bat "${toolNuget} restore"
      bat "${toolMsBuild} ${file.name} ${target}"
    }
  }
}

def slackNotify(slackChannel, buildColor, buildStage, buildFlag) {
  def author = bat returnStdout: true, script: "git log -1 --pretty=%%an"
  def branch = bat returnStdout: true, script: "git rev-parse --abbrev-ref=strict head"
  def commit = bat returnStdout: true, script: "git log -1 --pretty=\"%%s - %%h\""
  
  author = (author.split("\n"))[2]
  branch = (branch.split("\n"))[2]
  commit = (commit.split("\n"))[2]
  
  messageHead = "${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${buildStage} - ${buildFlag}"
  messageBody = "${author} - ${branch} - ${commit}"
  
  slackSend channel: slackChannel, color: buildColor, message: "${messageHead}\n${messageBody}"
}

stage("IMPORT") {
  node() {
    try {
      git branch: gitBranch, url: gitUrl
      slackNotify(slackChannel, buildColor[1], "IMPORT", buildFlag[1])
    } catch(error) {
      slackNotify(slackChannel, buildColor[0], "IMPORT", buildFlag[0])
      throw error
    }
  }
}

stage("ANALYZE") {
  node() {
    try {
      dotnetBuild("*.sln", "/t:rebuild")
      slackNotify(slackChannel, buildColor[1], "ANALYZE", buildFlag[1])
    } catch(error) {
      slackNotify(slackChannel, buildColor[0], "ANALYZE", buildFlag[0])
      throw error
    }
  }
}

stage("TEST") {
  node() {
    try {
      slackNotify(slackChannel, buildColor[1], "TEST", buildFlag[1])
    } catch(error) {
      slackNotify(slackChannel, buildColor[0], "TEST", buildFlag[0])
      throw error
    }
  }
}

stage("DEPLOY") {
  node() {
    try {
      dotnetBuild("*.csproj", "/t:package")
      slackNotify(slackChannel, buildColor[1], "DEPLOY", buildFlag[1])
    } catch(error) {
      slackNotify(slackChannel, buildColor[0], "DEPLOY", buildFlag[0])
      throw error
    }
  }
}

stage("EXPORT") {
  node() {
    try {
      slackNotify(slackChannel, buildColor[1], "EXPORT", buildFlag[1])
    } catch(error) {
      slackNotify(slackChannel, buildColor[0], "EXPORT", buildFlag[0])
      throw error
    }
  }
}
