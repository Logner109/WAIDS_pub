# Web Application Intrusion Detection System - WAIDS
### Overview
The purpose of the WAIDS was based on the analysis to design an Intrusion Detection System and to create an Administration panel for further analyzing and system administration. In this work we deal with the web traffic monitoring and with the processing and scanning HTTP requests for the security reasons. The main purpose of the request scanning is to detect a possible malicious payload input by the user. The final system is composed of two main parts, these are the agent for the packet sniffing and sending user requests to the IDS, and the admin IDS panel for system administration, REGEX rule management and traffic analysis.
### Content
| File              | Purpose       |
| ----------------- |:--------------:| 
|java/stu/fei/ids   | Java Maven project containing source codes of WAIDS administration panel |
|resources          | HTML source codes for appearance of WAIDS administration panel      |  
|WAIDS - Thesis.pdf | Thesis containing information about the project, including detailed description, workflow, diagrams, used technologies etc. (Slovak language) |
|request_agent.cpp  | C++ source code for intercepting chosen interface TCP traffic      |
|request_agent.conf | Configuration file for request_agent.cpp      |
### Building
The building process is written in the WAIDS - Thesis.pdf (Attachment C) among with some other pre-requisities and used technologies necessary for project building and initilization.
### Configuration
TODO



