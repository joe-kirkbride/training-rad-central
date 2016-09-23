// jenkins pipeline :: nodejs

def gitBranch = "**"
def gitUrl = "https://github.com/<account-name>/<repository-name>.git"

stage("IMPORT") {
  node() {
    try {
      git branch: gitBranch, url: gitUrl
    } catch(error) {
      throw error
    }
  }
}

stage("ANALYZE") {
  node() {
    try {
    } catch(error) {
      throw error
    }
  }
}

stage("TEST") {
  node() {
    try {
    } catch(error) {
      throw error
    }
  }
}

stage("DEPLOY") {
  node() {
    try {

    } catch(error) {
      throw error
    }
  }
}

stage("EXPORT") {
  node() {
    try {

    } catch(error) {
      throw error
    }
  }
}
