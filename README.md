# Jetpack-ViewBinding-Migrator-Plugin
An Jetpack compose desktop app to Migrate synthetic imports to Jetpack Viewbinding 

# WireFrames 
https://www.figma.com/file/KmQIjraCTA3STWIQLhMqcL/View-Binder-Wizard?node-id=0%3A1

# Build Project
1. Checkout main using git in intellij  
2. Gradle tab > Tasks > Compose Desktop > run 

#TODO

- Features
    - Analytics

- [ ] Privacy Policy && Disclaimer screen && Support Developer Screen
- [ ] Project config screen
    - [ ] validate project is valid android project
    - [ ] validate android gradle version is above specific version
    - [ ] Does project have multi module?
    - [ ] Is there a common module shared among all module?
        - [ ] if yes -> add base activities to core module
        - [ ] No -> create base module
            - [ ] add core module to all module
    - [ ] project is single module
        - [ ] if yes -> take base folder path
        - [ ] No -> create base folder

- [ ] add check list to migrate
    - [ ] migrate activity [experimental]
    - [ ] migrate fragment [experimental]
    - [ ] migrate Recycler View Adapter [experimental]
    - [ ] Migrate custom view [experimental]
    
- [ ] migrate activity dialog option
    - [ ] base activity + package name
    - [ ] select resource id format 
        - if seak-casing select yes
        - if if camel casing select yes
        - else other -> enter regex
    
- [ ] migrate Fragment dialog option
    - [ ] base fragment + package name
    - [ ] select resource id format 
        - if seak-casing select yes
        - if if camel casing select yes
        - else other -> enter regex


