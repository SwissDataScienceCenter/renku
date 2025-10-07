# Sessions

## Overview

List of containers and a short explanation for each for user sessions.

### Containers:
- **amalthea-session**: The container that is running the VS code or other frontend the user accesses.
- **authproxy**: Used to authenticate sessions for anonymous users and to track idleness.
- **oauth2-proxy**: Used to authenticated sessions for registered users.
- **git-proxy**: Used to inject git credentials into requests coming from `amalthea-session`.

### Init containers:
- **git-clone**: Clones all repositories used in the session. 
- **init-certificates**: Injects CA certificates into all other containers.

## Networking

### Registered user

```mermaid
flowchart LR
    Ingress
    subgraph Session
        User[amalthea-session]
        AP[authproxy]
        OP[oauth2-proxy]
        GP[git-proxy]
    end
    Ingress --/sessions/unique-session-id--> OP
    OP --> AP
    AP --> User
    OP -.- KC(Keycloak)
```

### Anonymous user

```mermaid
flowchart LR
    Ingress
    subgraph Session
        User[amalthea-session]
        AP[authproxy]
        GP[git-proxy]
    end
    Ingress --/sessions/unique-session-id--> AP
    AP --> User
```

### Git access

```mermaid
flowchart LR
    subgraph Session
        User[amalthea-session]
        AP[authproxy]
        OP[oauth2-proxy]
        GP[git-proxy]
    end
    User --push, pull, fetch--> GP
    GP --> Git[Gitlab, Github, etc.]
```
