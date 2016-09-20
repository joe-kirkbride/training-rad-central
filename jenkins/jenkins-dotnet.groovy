// jenkins pipeline :: dotnet

def buildColor = [green: "#00AA00", red: "#AA0000"]
def buildFlag = [failing: "FAIL", passing: "PASS"]
def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def projectKey = ""
def projectName = ""
def projectVersion = ""
def slackChannel = "#<channel>"
def toolCurl = "<path-to-curl>"
def toolMsDeploy = "<path-to-msdeploy>"
def toolSonarQube = "<path-to-sonarqube>"

def dotnetBuild(extension, target, restore = false) {
  def files = findFiles glob: "**/${extension}"
  def path = ""
  def toolMsBuild = "<path-to-msbuild>"
  def toolNuget = "<path-to-nuget>"

  for (file in files.toList()) {
    path = file.path.replace(file.name, "")

    dir("${path}") {
      if (restore) {
        bat "${toolNuget} restore"
      }
      
      bat "${toolMsBuild} ${file.name} ${target}"
    }
  }
}

def slackNotify(buildChannel, buildColor, buildStage, buildFlag) {
  def author = bat returnStdout: true, script: "git log -1 --pretty=%%an"
  def branch = bat returnStdout: true, script: "git rev-parse --abbrev-ref=strict head"
  def commit = bat returnStdout: true, script: "git log -1 --pretty=\"%%s - %%h\""
  
  author = (author.split("\n"))[2]
  branch = (branch.split("\n"))[2]
  commit = (commit.split("\n"))[2]
  
  messageHead = "${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${buildStage} - ${buildFlag}"
  messageBody = "${author} - ${branch} - ${commit}"
  
  slackSend channel: buildChannel, color: buildColor, message: "${messageHead}\n${messageBody}"
}

def sonarqubeScan(action) {
  switch(action) {
    case "begin":
      bat "${toolSonarQube} begin /k:\"${projectKey}\" /n:\"${projectName}\" /v:\"${projectVersion}\""
    break
    case "end":
      bat "${toolSonarQube} end"
    break
  }
}

stage("IMPORT") {
  node() {
    try {
      git branch: gitBranch, url: gitUrl
      slackNotify(slackChannel, buildColor.green, "IMPORT", buildFlag.passing)
    } catch(error) {
      slackNotify(slackChannel, buildColor.red, "IMPORT", buildFlag.failing)
      throw error
    }
  }
}

stage("ANALYZE") {
  node() {
    try {
      dotnetBuild("*.sln", "/t:rebuild", true)
      slackNotify(slackChannel, buildColor.green, "ANALYZE", buildFlag.passing)
    } catch(error) {
      slackNotify(slackChannel, buildColor.red, "ANALYZE", buildFlag.failing)
      throw error
    }
  }
}

stage("TEST") {
  node() {
    try {
      slackNotify(slackChannel, buildColor.green, "TEST", buildFlag.passing)
    } catch(error) {
      slackNotify(slackChannel, buildColor.red, "TEST", buildFla.failing)
      throw error
    }
  }
}

stage("DEPLOY") {
  node() {
    try {
      dotnetBuild("*.csproj", "/t:package")
      slackNotify(slackChannel, buildColor.green, "DEPLOY", buildFlag.passing)
    } catch(error) {
      slackNotify(slackChannel, buildColor.red, "DEPLOY", buildFlag.failing)
      throw error
    }
  }
}

stage("EXPORT") {
  node() {
    try {
      slackNotify(slackChannel, buildColor.green, "EXPORT", buildFlag.passing)
    } catch(error) {
      slackNotify(slackChannel, buildColor.red, "EXPORT", buildFlag.failing)
      throw error
    }
  }
}
