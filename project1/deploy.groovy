pipeline {
    agent any

    parameters {
        string(
            name: 'SERVER_IP',
            defaultValue: '3.80.113.64',
            description: 'Enter server IP address'
        )
    }

    environment {
        SSH_KEY64 = credentials('SSH_KEY64')
    }

    stages {

        stage('Configure SSH') {
            steps {
               sh
               '''
                mkdir -p ~/.ssh
                chmod 700 ~/.ssh

                echo -e "Host *\\n\\tStrictHostKeyChecking no\\n" > ~/.ssh/config
                chmod 600 ~/.ssh/config

                touch ~/.ssh/known_hosts
                chmod 600 ~/.ssh/known_hosts

                cat ~/.ssh/config
               '''
            }
        }

        stage('Prepare SSH Key') {
            steps {
               sh 
               '''
                echo "$SSH_KEY64" > mykey.pem
                chmod 600 mykey.pem
                ssh-keygen -R ${SERVER_IP} || true
               '''
            }
        }

        stage('Deploy Code to Server') {
            steps {
                sh 
               '''
                ssh -i mykey.pem ubuntu@${SERVER_IP} \
                "cd /var/www/html && git pull origin main"
               '''
            }
        }
    }
}
