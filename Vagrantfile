# -*- mode: ruby -*-
# vi: set ft=ruby :

$provisionScript = <<SCRIPT
  export DEBIAN_FRONTEND=noninteractive
  apt-get update > /dev/null
  apt-get -y install ncftp exuberant-ctags vim-nox ack-grep git make ruby ruby-dev unzip tmux
  apt-get -y install openjdk-6-jdk
  apt-get -y install postgresql-client-common postgresql-client-9.1

  if [ ! -d /home/hunter/vimrc-base ]; then
    cd /home/hunter &&
    su -c "git clone git@github.com:vokhotnikov/vimrc-base.git" hunter &&
    cd vimrc-base &&
    su -c "git checkout scala" hunter &&
    su -c "git submodule init" hunter &&
    su -c "git submodule update" hunter &&
    su -c "./setup.sh" hunter
  fi

  if [ ! -f /home/hunter/.vimrc ]; then
    echo "source /home/hunter/vimrc-base/vimrc" > /home/hunter/.vimrc
    chown hunter /home/hunter/.vimrc
  fi

  if [ ! -f /home/hunter/.gitconfig ]; then
    echo "[user]" > /home/hunter/.gitconfig
    echo "    name = Vladimir Okhotnikov" >> /home/hunter/.gitconfig
    echo "    email = <vokhotnikov@gmail.com>" >> /home/hunter/.gitconfig
    chown hunter /home/hunter/.gitconfig
    echo "gitconfig created"
  fi

  if [ ! -f /home/hunter/.bash_profile ]; then
    echo "export EDITOR=/usr/bin/vim" >> /home/hunter/.bash_profile
    echo "export PATH=$PATH:/home/hunter/play-2.1.1" >> /home/hunter/.bash_profile
    echo "alias tmux=\\"tmux -2\\"" >> /home/hunter/.bash_profile
    chown hunter /home/hunter/.bash_profile
  fi

  if [ ! -f /home/hunter/.tmux.conf ]; then
    echo "set-option -g status-utf8 on" >> /home/hunter/.tmux.conf
    echo "set-option -g prefix C-a" >> /home/hunter/.tmux.conf
    echo "bind-key C-a last-window" >> /home/hunter/.tmux.conf
    echo "set -g base-index 1" >> /home/hunter/.tmux.conf
    echo "set-window-option -g mode-keys vi" >> /home/hunter/.tmux.conf
    echo "bind-key -t vi-copy 'v' begin-selection" >> /home/hunter/.tmux.conf
    echo "bind-key -t vi-copy 'y' copy-selection" >> /home/hunter/.tmux.conf
    echo "set -g mode-mouse off" >> /home/hunter/.tmux.conf
    echo "set -g default-terminal \\"screen-256color\\"" >> /home/hunter/.tmux.conf
    chown hunter /home/hunter/.tmux.conf
  fi

  if [ ! -f /home/hunter/play-2.1.1.zip ]; then
    echo "Downloading play framework"
    wget -c http://downloads.typesafe.com/play/2.1.1/play-2.1.1.zip -O /home/hunter/play-2.1.1.zip -q
    chown hunter:hunter /home/hunter/play-2.1.1.zip
    echo "Download complete"
  fi

  echo "Provisioning completed"
SCRIPT

Vagrant.configure("2") do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "webdevbase"

  # The url from where the 'config.vm.box' box will be fetched if it
  # doesn't already exist on the user's system.
  # config.vm.box_url = "http://files.vagrantup.com/precise32.box"
  config.vm.box_url = "/Volumes/vboximg/myprecise32.box"

  config.vm.hostname = "PlayTutorial"

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  config.vm.network :forwarded_port, guest: 9000, host: 9000

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network :private_network, ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network :public_network

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  config.vm.provider :virtualbox do |vb|
  #   # Don't boot with headless mode
  #   vb.gui = true
  #
    # Use VBoxManage to customize the VM. For example to change memory:
    vb.customize ["modifyvm", :id, "--memory", "1024"]
    vb.customize ["modifyvm", :id, "--cpus", "4"]
  end
  #
  # View the documentation for the provider you're using for more
  # information on available options.

  # Enable provisioning with Puppet stand alone.  Puppet manifests
  # are contained in a directory path relative to this Vagrantfile.
  # You will need to create the manifests directory and a manifest in
  # the file sevstone-dev.pp in the manifests_path directory.
  #
  # An example Puppet manifest to provision the message of the day:
  #
  # # group { "puppet":
  # #   ensure => "present",
  # # }
  # #
  # # File { owner => 0, group => 0, mode => 0644 }
  # #
  # # file { '/etc/motd':
  # #   content => "Welcome to your Vagrant-built virtual machine!
  # #               Managed by Puppet.\n"
  # # }
  #
  # config.vm.provision :puppet do |puppet|
  #   puppet.manifests_path = "manifests"
  #   puppet.manifest_file  = "sevstone-dev.pp"
  # end

  # Enable provisioning with chef solo, specifying a cookbooks path, roles
  # path, and data_bags path (all relative to this Vagrantfile), and adding
  # some recipes and/or roles.
  #
  # config.vm.provision :chef_solo do |chef|
  #   chef.cookbooks_path = "../my-recipes/cookbooks"
  #   chef.roles_path = "../my-recipes/roles"
  #   chef.data_bags_path = "../my-recipes/data_bags"
  #   chef.add_recipe "mysql"
  #   chef.add_role "web"
  #
  #   # You may also specify custom JSON attributes:
  #   chef.json = { :mysql_password => "foo" }
  # end

  # Enable provisioning with chef server, specifying the chef server URL,
  # and the path to the validation key (relative to this Vagrantfile).
  #
  # The Opscode Platform uses HTTPS. Substitute your organization for
  # ORGNAME in the URL and validation key.
  #
  # If you have your own Chef Server, use the appropriate URL, which may be
  # HTTP instead of HTTPS depending on your configuration. Also change the
  # validation key to validation.pem.
  #
  # config.vm.provision :chef_client do |chef|
  #   chef.chef_server_url = "https://api.opscode.com/organizations/ORGNAME"
  #   chef.validation_key_path = "ORGNAME-validator.pem"
  # end
  #
  # If you're using the Opscode platform, your validator client is
  # ORGNAME-validator, replacing ORGNAME with your organization name.
  #
  # If you have your own Chef Server, the default validation client name is
  # chef-validator, unless you changed the configuration.
  #
  #   chef.validation_client_name = "ORGNAME-validator"

  config.vm.provision :shell, :inline => $provisionScript
end
