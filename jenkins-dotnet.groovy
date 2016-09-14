// jenkins pipeline :: dotnet

def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"

stage("import") {
  node() {
    try {
      git branch: gitBranch, url: gitUrl
    } catch(error) {
      throw error
    }
  }
}

stage("analyze") {
  node() {
    try {

    } catch(error) {
      throw error
    }
  }
}

stage("test") {
  node() {
    try {

    } catch(error) {
      throw error
    }
  }
}

stage("deploy") {
  node() {
    try {

    } catch(error) {
      throw error
    }
  }
}

stage("export") {
  node() {
    try {

    } catch(error) {
      throw error
    }
  }
}
