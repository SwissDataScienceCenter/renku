---
title: User Management
---

## Overview

Renku uses [Keycloak](https://www.keycloak.org/) as its identity provider. When Renku is installed, the `init-keycloak-realms` Kubernetes job automatically initialises the Renku realm in Keycloak and sets up the necessary accounts, roles, and permissions.

## Administrative Access

### Accessing Keycloak Admin Console

1. Navigate to your Keycloak instance (at `https://<your-domain>/auth`)
2. Click on "Administration Console"
3. Log in with your admin credentials
4. Select the Renku realm to manage users and roles

### Default Admin Credentials

The default admin username and password are stored in the `keycloak-password-secret` Kubernetes secret when Renku is installed. To retrieve these credentials:

```bash
kubectl get secret keycloak-password-secret -n renku -o jsonpath='{.data.KEYCLOAK_ADMIN}' | base64 -d
```

```bash
kubectl get secret keycloak-password-secret -n renku -o jsonpath='{.data.KEYCLOAK_ADMIN_PASSWORD}' | base64 -d
```

### Admin Role Assignment

Users with the `renku-admin` role can perform administrative tasks in Renku, including:

- Creating and managing resource pools
- Configuring platform integrations

## Managing Renku Admin Users

To grant administrative privileges to additional users:

1. Access the Keycloak Admin Console
2. Navigate to the Renku realm
3. Go to **Users** and select the target user
4. In the **Role Mapping** tab, assign the `renku-admin` role

## Managing Keycloak Admin Users

In addition to Renku-specific admin user, you may want to create additional Keycloak admin accounts for managing the identity provider itself. 

:::danger[Keycloak admins have very broad permissions]
Assigning someone to be a Keycloak admin gives them full access to every realm in the Keycloak deployment and the ability to create, impersonate and delete any user in any realm, as well as make any user a Renku admin. Use this with caution.
:::

Note that making someone a Keycloak admin does not automatically make them a Renku admin or vice versa. The two roles are completely independent. But a Keycloak admin can impersonate a user that has the Renku admin role. In addition, Keycloak admins have the permission to assign the Renku admin role to themselves or any other user.

### Creating Additional Keycloak Admins

1. Access the Keycloak Admin Console with existing admin credentials
2. Switch to the **master** realm (use the realm dropdown in the top-left)
3. Go to **Users** and click **Add user**
4. Fill in the required user details
5. Set a password in the **Credentials** tab
6. Click **Save**

### Assigning Keycloak Admin Roles

1. After creating the user, go to the **Role Mapping** tab
2. Assign the `admin` role to the user.

## Basic User Management Tasks

### Deleting Users

To fully remove a user from both Keycloak and Renku:

#### Step 1: Get the User's Keycloak ID

1. Access the Keycloak Admin Console
2. Navigate to the Renku realm
3. Go to **Users** and search for the user
4. Click on the username to open their profile
5. Copy the user ID from the URL or the user details (this is the Keycloak ID)

#### Step 2: Delete from Renku Platform

1. Navigate to the Renku Swagger page (at `https://<your-domain>/swagger`)
2. Use the user deletion endpoint with the Keycloak ID obtained in Step 1
3. Execute the API call to remove the user from Renku's database

#### Step 3: Delete from Keycloak

1. Return to the user's profile in Keycloak Admin Console
2. Click **Delete** at the top of the user details page
3. Confirm the deletion when prompted

:::info[Complete Deletion]
Deleting only from Keycloak will not remove the user's data from Renku. Use both steps to ensure complete removal.
:::

### Disabling Users

To temporarily disable a user account without deleting it:

1. Open the user's profile in the Keycloak Admin Console
2. Toggle **Enabled** to `OFF`
3. Click **Save**

:::warning[Token Validity]
Disabling a user does not invalidate existing valid tokens. The user will still have access to Renku until their tokens expire. However, they will not be able to launch new Renku sessions.
:::

### Resetting User Passwords

To reset a user's password:

1. Navigate to the user's profile
2. Go to the **Credentials** tab
3. Click **Reset Password**
4. Set a new temporary password
5. Ensure **Temporary** is `ON` to force password change on next login
6. Click **Set Password**

### Other Keycloak Operations and Configuration

Please refer to the [official Keycloak documentation](https://www.keycloak.org/documentation) for more guidance on managing users and configuring Keycloak.
