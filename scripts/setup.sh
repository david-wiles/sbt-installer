#!/bin/sh

# Install sbt
if ! command -v sbt &> /dev/null
then

  if command -v brew &> /dev/null
  then
    brew install sbt
  elif command -v sdk &> /dev/null
  then
    sdk install sbt
  elif command -v apt-get &> /dev/null
  then
    sudo apt-get update
    sudo apt-get install apt-transport-https curl gnupg -yqq
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
    sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
    sudo apt-get update
    sudo apt-get install sbt
  elif command -v yum &> /dev/null
  then
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo yum install sbt
  else
    echo "Unable to install sbt. Please install manually: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html"
    exit 1
  fi

fi

# Make default directories
mkdir -p $HOME/.local/bin $HOME/.local/opt

# Check if ~/.local/bin is already on the path
case ":$PATH:" in
  *:$HOME/.local/bin:*) ;;
  *)
    # Add to profiles
    if [ -f "$HOME/.bashrc" ]; then
        echo "export PATH=\$PATH:$HOME/.local/bin" >> $HOME/.bashrc
    fi

    if [ -f "$HOME/.zshrc" ]; then
        echo "export PATH=\$PATH:$HOME/.local/bin" >> $HOME/.zshrc
    fi

    if [ -f "$HOME/.profile" ]; then
        echo "export PATH=\$PATH:$HOME/.local/bin" >> $HOME/.profile
    fi

    if [ -f "$HOME/.config/fish/config.fish" ]; then
      echo "fish_add_path $HOME/.local/bin" >> $HOME/.config/fish/config.fish
    fi

    # Add to current shell
    echo "To update the path of your current shell, run the following command:"
    echo "export PATH=\$PATH:\$HOME/.local/bin"
    ;;
esac

