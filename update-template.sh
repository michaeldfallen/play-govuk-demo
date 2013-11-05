thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$thisDir"

function removeSubmodule() {
  submodule="$(cat .gitmodules | grep "path = .*$1" | grep -Eo "\S*$1")"
  if [[ -n $submodule ]]; then 
    rm -rv "$submodule"
    rm -rv ".gitmodules"
    rm -rv ".git/modules/$1"
    git rm "$submodule"
  fi
}

function checkSubmoduleExists() {
  submoduleUrl=$1
  submoduleName=$2
  if [ -z "$(cat .gitmodules | grep "$submoduleUrl")" ]; then
    if [ -n "$(cat .gitmodules | grep "$submoduleName")" ]; then
      removeSubmodule "$submoduleName"
    fi
    git submodule add "$submoduleUrl" $submoduleName
  fi
}

checkSubmoduleExists "https://github.com/alphagov/govuk_template_play.git" "app/assets/govuk_template_play"

echo "Updating govuk_template_play"
git submodule init
git submodule update
