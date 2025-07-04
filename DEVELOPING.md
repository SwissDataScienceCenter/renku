# Renku Software Development Guidelines

This document outlines secure software development practices and guidelines on the Renku project that the developer team should follow.

This document is based in part on [ISO 27001](https://hyperproof.io/iso-27001/)  and [NIST SSDF](https://nvlpubs.nist.gov/nistpubs/CSWP/NIST.CSWP.04232020.pdf) as well as the [Secure development and deployment guidance](https://www.ncsc.gov.uk/collection/developers-collection).

## Security Requirements

- Implement access control mechanisms to restrict unauthorized access to systems and data
- Input validation: All user input has to be validated to prevent injection attacks like SQL injection and cross site scripting
- Secure communication: Sensitive data has to be sent over encrypted channels (e.g. HTTPS/SSL) between client and server. Sensitive user data has to be encrypted at rest
- User sessions must be secured against hijacking and other session related attacks
- Applications must implement robust error handling to prevent disclosure of sensitive information and detailed logs have to be maintained for security monitoring and incident response
- Code reviews must be done for production code and whenever possible, automated security testing should be added to repositories
- Code must be modular to enable reuse and updates
- Security related functionality should be tested with automated tests to ensure correct functionality
- Software should be regularly updated with latest security patches to address vulnerabilities
- Configuration values must be set with secure defaults and configuration changes must be reviewed with security in mind
- Only services that are need direct user interaction should be exposed to the internet, with proper access controls. Components handling highly sensitive data should not be accessible on the internet.

## People

Employees are tested for their technological skill during the hiring process and their background is checked. This involves interviews with several software engineers (3-5 interviews), checking references and completing coding tasks.

Employees are split into Developers and Administrators, with some Administrators also fulfilling a Developer role.

Developers are only given access to repositories that they are working on. Repositories and systems containing sensitive values are only accessible to Administrators. Only a subset of Administrators has access to all systems and sensitive data, with other Administrators being limited to such systems as are needed for their tasks.

If an employee leaves the organization, all credentials and secrets that they had access to are revoked and replaced with new ones, and all privileged access to systems as well as internal tools is revoked.

## Processes

Changes to software require at least one review by another developer. Releases of the platform require a review by an Administrator and a Product Owner.

All sensitive data and credentials are encrypted and stored securely, with only Administrators having full access, if necessary for their role.

The development lifecycle takes place in three different environments, development, infra-staging and production. All developers can create development environments to create and test changes in. Changes to the testing environment can only be made after review and approval by an Administrator and Product Owner, after which changes are automatically deployed to testing. Rollout to production is done by Administrators, after reviewing changes and testing in the testing environment. Rollout to production is taken with care to the disruption to users that it might cause, and after rollout it is closely monitored to ensure operational status.

Employees must notify Administrators of any security issues and clearly communicate risks and threats.

## Technology

The following programming languages are used in the development of Renku components:

- Go
- Python
- Rust
- Scala
- Typescript

Additional languages such as Bash or make can be used for specific tasks like building software, running CI and deployment.

Developers must follow best practices for each language and put in place quality controls like linting, code formatting, automated dependency updates, testing, vulnerability scanners and type checking.

Developers should follow [OWASP recommendations](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/assets/docs/OWASP_SCP_Quick_Reference_Guide_v21.pdf) when developing code. In general, Developers must follow security best practices, implementing hardening techniques to keep the attack surface of the application small. Every developer must read through the [OWASP Top 10](https://owasp.org/www-project-top-ten/) to make sure they’re familiar with the most common security issues.

The software architecture should be modular, keeping dependencies between parts of the platform to a minimum and ensuring clear responsibility of pieces of code. Security relevant code (encryption, authentication, authorization) should be handled separately from other application code. Functions and code should be documented, highlighting potential security implications.

All code is stored in source control repositories (git) on Github. Code is generally publicly accessible unless it contains sensitive information, with write access being limited to Developers according to their roles. This allows tracking of who made changes and prevents unauthorized changes.

### Security Tools

Use snyk to scan our container images


