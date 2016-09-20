// jenkins pipeline :: dotnet

def buildColor = [green: "#00AA00", red: "#AA0000"]
def buildFlag = [failing: "FAIL", passing: "PASS"]
def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def slackChannel = "#<channel>"
def toolMsBuild = "<path-to-msbuild>"
def toolMsDeploy = "<path-to-msdeploy>"

def dotnetAnalyze() {
  def analysisReport = ""
  def projectUrl = "<path-to-analysis>"
  def toolCurl = "<path-to-curl>"

  bat "${toolCurl} ${projectUrl} -o analysis-report.json"
  analysisReport = readFile "analysis-report.json"
  
  if (analysisReport.indexOf("ERROR") != -1 && analysisReport.indexOf("ERROR") <= 35) {
    error "the sonarqube code quality failed."
  }
}

def dotnetBuild(toolMsBuild, extension) {
  def files = findFiles glob: "**/${extension}"
  def path = ""
  def projectKey = ""
  def projectName = ""
  def projectVersion = ""
  def toolNuget = "<path-to-nuget>"
  def toolSonarQube = "<path-to-sonarqube>"

  for (file in files.toList()) {
    path = file.path.replace(file.name, "")

    dir("${path}") {
      bat "${toolSonarQube} begin /k:\"${projectKey}\" /n:\"${projectName}\" /v:\"${projectVersion}\""
      bat "${toolNuget} restore"
      bat "${toolMsBuild} ${file.name} /t:rebuild"
      bat "${toolSonarQube} end"
    }
  }
}

def dotnetPackage(toolMsBuild, extension) {
  def files = findFiles glob: "**/${extension}"
  def path = ""

  for (file in files.toList()) {
    path = file.path.replace(file.name, "")

    dir("${path}") {
      bat "${toolMsBuild} ${file.name} /t:package"
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
      dotnetBuild(toolMsBuild, "*.sln")
      sleep time: 10, units: "SECONDS"
      dotnetAnalyze()
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
      dotnetPackage(toolMsBuild, "*.csproj")
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
