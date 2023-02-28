# FlowTime

Flowtime is a tool to help tabulate/organize work sessions, break sessions using the Flowtime Technique.  
It is created using the kotlin-multiplatform--react--MUI Stack.

# Install
Use the gradle wrapper to build and install Flowtime
```bash
ORG_GRADLE_PROJECT_isProduction=true
./gradlew installDist
```
# 

# Usage

1) Run the build jar and connect to localhost:9090 in a browser of your choice

2) Create Work/Break Sessions using the timers in the Timer Tab:


![Timer Tab](/imgs/TimerTab.png)

3) To record certain distractions that occured in your sessions, create corresponding tags in the Distractions Tab. You may add them on the right hand side of the Timer's tab:


![Distraction Tab](/imgs/DistractionsTab.png)

4) See the statistics of your work and break sessions, as well as your distractions in the statistics tab:


![Statistics Tab](/imgs/StatsTab.png)
