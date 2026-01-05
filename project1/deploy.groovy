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
                sh '''
                mkdir -p ~/.ssh
                chmod 700 ~/.ssh

                              cat > ~/.ssh/config <<'EOF'
Host *
    StrictHostKeyChecking no
EOF
                chmod 600 ~/.ssh/config

                touch ~/.ssh/known_hosts
                chmod 600 ~/.ssh/known_hosts

                cat ~/.ssh/config
                '''
            }
        }

        stage('Prepare SSH Key') {
            steps {
                sh '''
                  echo "$SSH_KEY64" > /tmp/jenkins_keys/myKey.pem
                  cat /tmp/jenkins_keys/myKey.pem
                chmod 600 /tmp/jenkins_keys/myKey.pem
                ssh-keygen -R ${params.SERVER_IP}
                '''
            }
        }

        stage('Deploy Code to Server') {
            steps {
                sh '''
                ssh -i/tmp/jenkins_keys/myKey.pem ubuntu@${params.SERVER_IP} \
                "cd /var/www/html && git pull origin main"
                '''
            }
        }
    }
}
