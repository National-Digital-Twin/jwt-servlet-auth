# README

**Repository:** `jwt-servlet-auth`  
**Description:** `This repository provides libraries that allow adding JSON Web Token (JWT) based Bearer authentication into
Java Servlet applications.`

<!-- SPDX-License-Identifier: Apache-2.0 AND OGL-UK-3.0 -->

## Overview
This repository contributes to the development of **secure, scalable, and interoperable data-sharing infrastructure**. It supports NDTP’s mission to enable **trusted, federated, and decentralised** data-sharing across organisations.

This repository is one of several open-source components that underpin NDTP’s **Integration Architecture (IA)**—a framework designed to allow organisations to manage and exchange data securely while maintaining control over their own information. The IA is actively deployed and tested across multiple sectors, ensuring its adaptability and alignment with real-world needs.

For a complete overview of the Integration Architecture (IA) project, please see the [Integration Architecture Documentation](https://github.com/National-Digital-Twin/integration-architecture-documentation).

### JSON Web Token (JWT) Servlet Authentication

Support is provided for both Servlet 3 (`javax.servlet`), Servlet 5 (`jakarta. servlet`) and
JAX-RS 3 (`jakarta.ws.rs`) based applications.

While some servlet containers provide integrated support for this, others don't, and even where support is provided the
flexibility of OAuth 2 (the specification that defined Bearer auth) can still leave implementations lacking.

[JWT][Rfc7519] based [Bearer Authentication][Rfc6750], while very simple at a high level, actually proves to be quite difficult in practise
because tokens can be issued by a huge variety of issuers using different signature algorithms, key management
techniques etc. To try and address this the library provides two main abstractions:

- `JwtAuthenticationEngine` - This is an abstract base class that implements the authentication flow with abstract
  methods provided that perform configurable portions of the flow e.g. selecting the HTTP Header(s) that convey the
  JWTs, finding the username from the verified token and transforming the request with the authenticated user identity.
- `JwtVerifier` - This is an interface for verifying tokens.

The aim being to decouple the flow of obtaining a token from verifying it, allowing the engine to be easily
repurposed for multiple servlet container runtime versions.

Concrete implementations of both are provided, see [Usage](INSTALLATION.md#usage) for example usage.

## Prerequisites
Before using this repository, ensure you have the following dependencies installed:
- **Required Tooling:**
    - JDK 17+
    - Apache Maven 3.8.1+
- **System Requirements:**
    - JDK 17+
    - Apache Maven 3.8.1+

## Quick Start
Follow these steps to get started quickly with this repository. For detailed installation, configuration, and deployment, refer to the relevant MD files.

### 1. Download
```sh  
git clone https://github.com/National-Digital-Twin/jwt-servlet-auth.git
cd jwt-servlet-auth 
```
### 2. Build
```sh  
mvn clean install 
```

### 3. Full Installation
Refer to [INSTALLATION.md](INSTALLATION.md) for more details, including required dependencies and setup configurations.

## Features
- **Core functionality**
  - Enables secure JWT-based Bearer authentication for Servlet 3 (javax.servlet), Servlet 5 (jakarta.servlet), and JAX-RS 3 (jakarta.ws.rs) applications. 
  - Provides `JwtAuthFilter`, which must be added based on the servlet runtime. 
  - Implements authentication via the `JwtAuthenticationEngine`.
  - Supports JWKS verification, including `CachedJwksKeyLocator` for efficient key retrieval. 
- **Key integrations**
  - Compatible with OAuth 2.0 / OpenID Connect (OIDC) for standard JWT authentication. 
  - Supports JWKS-based verification, allowing dynamic key fetching from a URL or local file. 
  - Provides AWS Cognito & ELB integration for AWS-specific authentication. 
  - Uses the [jjwt](https://github.com/jwtk/jjwt) library for JWT parsing and verification, with jjwt-jackson for JSON processing. 
  - Works with Servlet 3 and 5, with adaptability for Servlet 6 using the Servlet 5 module.
- **Scalability & performance**
  - Flexible runtime support, working across different servlet versions with provided dependencies.
  - Cached JWKS verification minimizes key-fetching overhead for better performance.
- **Modularity**
  - Designed as a modular component of NDTP’s Integration Architecture (IA).
  - Usable as a library in other projects via Maven dependency management.


## Public Funding Acknowledgment
This repository has been developed with public funding as part of the National Digital Twin Programme (NDTP), a UK Government initiative. NDTP, alongside its partners, has invested in this work to advance open, secure, and reusable digital twin technologies for any organisation, whether from the public or private sector, irrespective of size.

## License
This repository contains both source code and documentation, which are covered by different licenses:
- **Code:** Originally developed by Telicent UK Ltd, now maintained by National Digital Twin Programme. Licensed under the [Apache License 2.0](LICENSE.md).
- **Documentation:** Licensed under the [Open Government Licence (OGL) v3.0](OGL_LICENSE.md).

By contributing to this repository, you agree that your contributions will be licensed under these terms.

See [LICENSE.md](LICENSE.md), [OGL_LICENSE.md](OGL_LICENSE.md), and [NOTICE.md](NOTICE.md) for details.

## Security and Responsible Disclosure
We take security seriously. If you believe you have found a security vulnerability in this repository, please follow our responsible disclosure process outlined in [SECURITY.md](SECURITY.md).

## Contributing
We welcome contributions that align with the Programme’s objectives. Please read our [Contributing](CONTRIBUTING.md) guidelines before submitting pull requests.

## Acknowledgements
This repository has benefited from collaboration with various organisations. For a list of acknowledgments, see [ACKNOWLEDGEMENTS.md](ACKNOWLEDGEMENTS.md).

## Support and Contact
For questions or support, check our Issues or contact the NDTP team on ndtp@businessandtrade.gov.uk.

[Rfc7519]: https://datatracker.ietf.org/doc/html/rfc7519
[Rfc6750]: https://datatracker.ietf.org/doc/html/rfc6750

**Maintained by the National Digital Twin Programme (NDTP).**

© Crown Copyright 2025. This work has been developed by the National Digital Twin Programme and is legally attributed to the Department for Business and Trade (UK) as the governing entity.
