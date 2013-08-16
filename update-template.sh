thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
templateDir="$thisDir/govuk_template_play"

echo "Updating govuk_template_play"
git submodule init
git submodule update