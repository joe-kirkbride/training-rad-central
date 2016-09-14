// jenkins pipeline :: dotnet

def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"

stage("import") {
  node() {
    git branch: gitBranch, url: gitUrl
  }
}

stage("analyze") {
  node() {

  }
}

stage("test") {
  node() {

  }
}

stage("deploy") {
  node() {

  }
}

stage("export") {
  node() {

  }
}
