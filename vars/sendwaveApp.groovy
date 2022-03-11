def call(String repoUrl){
       pipeline{
	agent any 
	tools {maven 'maven'}
		stages{
			stage('git-clone') {
				steps{
					checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'git-Jenkins', url: 'https://github.com/ETECH-Co/module2_ci.git']]])
				}
			}
			stage('Hello-Etech'){
				steps{
				sh 'git version'
				}
			}
			stage('codebuild'){
				steps{
				sh 'mvn clean package -DskipTests=true'
				}
			}
			stage('Unit Tests - JUnit and JaCoCo'){
				steps{
					sh 'mvn test'
					sh 'mvn -v'
				}
				post{
					always{
						junit'target/surefire-reports/*.xml'
						jacoco execPattern: 'target/jacoco.exec'
					}
				}
			}
						stage('Mutation Tests - PIT'){
  				steps{
   					sh "mvn org.pitest:pitest-maven:mutationCoverage"
    					}
 //    				post {
 //       				always {
 //         					pitmutation mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
 //      				}
 //    				}
   			}
				stage('CodeQuality - SAST'){
					steps{
					sh 'mvn clean verify sonar:sonar \
 					-Dsonar.projectKey=Devsecops-app \
  					-Dsonar.host.url=http://ajaam-tech.eastus.cloudapp.azure.com:9000 \
  					-Dsonar.login=6a1ab06cdd2d6c779012aa60f077576d0d2465da'
				}
			}	
		} 
  }
