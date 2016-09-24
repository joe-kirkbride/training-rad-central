// jenkins pipeline :: nodejs

def buildColor = [green: "#00AA00", red: "#AA0000"]
def buildFlag = [failing: "FAIL", passing: "PASS"]
def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"
def projectKey = ""
def slackChannel = "#<channel>"
def toolNpm = "<path-to-npm>"

def nodejsBuild(projectKey, toolNpm) {
  def files = findFiles glob: "**/package.json"
  def path = ""
  def projectName = ""
  def projectVersion = ""
  def toolSonarQube = "<path-to-sonarqube>"

  for (file in files.toList()) {
    path = file.path.replace(file.name, "")

    if (path.indexOf("node_modules") == -1) {
      dir("${path}") {
        bat "${toolNpm} install"
      }
    }
  }

  bat "${toolSonarQube} -Dsonar.projectKey=${projectKey} -Dsonar.projectName=${projectName} -Dsonar.projectVersion=${projectVersion} -Dsonar.sources=."
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
      nodejsBuild(projectKey, toolNpm)
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
      slackNotify(slackChannel, buildColor.red, "TEST", buildFlag.failing)
      throw error
    }
  }
}

stage("DEPLOY") {
  node() {
    try {
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
