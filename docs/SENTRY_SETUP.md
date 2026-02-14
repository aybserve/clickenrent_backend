# Sentry Setup Guide (Cloud Version)

Complete guide for setting up Sentry.io cloud error tracking and performance monitoring for ClickEnRent microservices.

> **💡 Looking for Self-Hosted Sentry (FREE)?**  
> See [SENTRY_SELF_HOSTED.md](SENTRY_SELF_HOSTED.md) for complete self-hosted deployment guide.  
> Self-hosted is 100% free with unlimited events and full control.

## Table of Contents

1. [Overview](#overview)
2. [Creating Sentry Account](#creating-sentry-account)
3. [Creating Projects](#creating-projects)
4. [Obtaining DSN Values](#obtaining-dsn-values)
5. [Environment Configuration](#environment-configuration)
6. [Configuring Alerts](#configuring-alerts)
7. [Setting Up Dashboards](#setting-up-dashboards)
8. [Team Management](#team-management)
9. [Testing the Integration](#testing-the-integration)
10. [Best Practices](#best-practices)

---

## Overview

ClickEnRent uses Sentry for:
- **Error Tracking**: Automatic capture and grouping of exceptions
- **Performance Monitoring (APM)**: Transaction tracing across services
- **Multi-Tenant Context**: Every error tagged with company/tenant information
- **Release Tracking**: Track errors across deployments
- **Smart Alerts**: Real-time notifications via Slack, Email, PagerDuty

**Architecture:**
- 7 separate Sentry projects (one per microservice)
- Each service reports to its own project for better organization
- Centralized monitoring dashboard to view all services

---

## Creating Sentry Account

### Step 1: Sign Up

1. Go to [https://sentry.io/signup/](https://sentry.io/signup/)
2. Choose your plan:
   - **Free**: 5K errors/month, 10K performance units/month (good for testing)
   - **Team**: $26/month - 50K errors/month, 100K performance units/month (recommended for production)
   - **Business**: $80/month - 250K errors/month, 500K performance units/month

### Step 2: Create Organization

1. After signup, create an organization (e.g., "ClickEnRent")
2. Choose a slug (e.g., "clickenrent" - this will be in your DSN URLs)

---

## Creating Projects

Create **7 separate projects** for each microservice:

### Project Setup

1. Click **"Create Project"** in Sentry dashboard
2. Choose platform: **Java** or **Spring Boot**
3. Set alert frequency: **Alert on every new issue**
4. Create the following projects:

| Project Name | Service | Description |
|--------------|---------|-------------|
| `clickenrent-auth` | auth-service | User authentication and OAuth |
| `clickenrent-rental` | rental-service | Bike rentals and operations |
| `clickenrent-payment` | payment-service | Payment processing |
| `clickenrent-support` | support-service | Customer support tickets |
| `clickenrent-notification` | notification-service | Push notifications |
| `clickenrent-search` | search-service | Elasticsearch search |
| `clickenrent-gateway` | gateway | API Gateway |

### Project Settings

For each project:

1. **General Settings:**
   - Environment: `production` (or `staging`, `development`)
   - Rate Limits: Default is fine
   - Data Scrubbing: Enabled (removes sensitive data)

2. **Performance Settings:**
   - Enable Performance Monitoring: ✅ Yes
   - Transaction Sampling Rate: 100% (for production monitoring)

---

## Obtaining DSN Values

### What is a DSN?

DSN (Data Source Name) is a unique URL that tells your application where to send error reports.

**Format:** `https://<public_key>@<org>.ingest.sentry.io/<project_id>`

### Getting Your DSN

For each project:

1. Go to **Settings → Projects → [Your Project]**
2. Click **Client Keys (DSN)**
3. Copy the **DSN** value
4. Save it for the corresponding service

**Example DSNs (fake values):**
```
SENTRY_DSN_AUTH=https://abc123def456@o123456.ingest.sentry.io/7891011
SENTRY_DSN_RENTAL=https://ghi789jkl012@o123456.ingest.sentry.io/7891012
SENTRY_DSN_PAYMENT=https://mno345pqr678@o123456.ingest.sentry.io/7891013
...
```

---

## Environment Configuration

### Step 1: Update .env File

Copy `.env.example` to `.env` and fill in your Sentry DSN values:

```bash
cp .env.example .env
nano .env
```

**Add your actual DSN values:**

```bash
# Sentry DSN Configuration
SENTRY_DSN_AUTH=https://YOUR_ACTUAL_DSN_HERE
SENTRY_DSN_RENTAL=https://YOUR_ACTUAL_DSN_HERE
SENTRY_DSN_SUPPORT=https://YOUR_ACTUAL_DSN_HERE
SENTRY_DSN_PAYMENT=https://YOUR_ACTUAL_DSN_HERE
SENTRY_DSN_NOTIFICATION=https://YOUR_ACTUAL_DSN_HERE
SENTRY_DSN_SEARCH=https://YOUR_ACTUAL_DSN_HERE
SENTRY_DSN_GATEWAY=https://YOUR_ACTUAL_DSN_HERE

# Sentry Environment
SENTRY_ENVIRONMENT=production

# Sentry Traces Sample Rate (1.0 = 100%)
SENTRY_TRACES_SAMPLE_RATE=1.0
```

### Step 2: Verify Configuration

Each service's `application.properties` already has Sentry configured to read from environment variables:

```properties
sentry.dsn=${SENTRY_DSN_AUTH}  # or SENTRY_DSN_RENTAL, etc.
sentry.environment=${SENTRY_ENVIRONMENT:production}
sentry.release=1.0-SNAPSHOT@auth-service
```

---

## Configuring Alerts

### Alert Rules

Set up alerts to get notified when errors occur:

1. Go to **Alerts → Create Alert Rule**
2. Choose alert type:

**Recommended Alert Rules:**

#### 1. First Seen Error Alert
- **When:** A new issue is first seen
- **Then:** Send notification to Slack/Email
- **For:** All services

#### 2. High Error Rate Alert
- **When:** More than 50 events in 1 hour
- **Then:** Send notification to Slack/Email/PagerDuty
- **For:** Critical services (auth, payment)

#### 3. Performance Degradation Alert
- **When:** P95 response time > 2 seconds
- **Then:** Send notification to Slack
- **For:** All services

### Notification Channels

#### Slack Integration

1. Go to **Settings → Integrations → Slack**
2. Click **Add to Slack**
3. Authorize Sentry to access your Slack workspace
4. Choose channel (e.g., `#alerts-production`)
5. Link to alert rules

#### Email Notifications

1. Go to **Settings → Notifications**
2. Configure email preferences:
   - New issues: Immediately
   - Issue frequency: Hourly digest
   - Workflow: Comment, assignment, resolution

#### PagerDuty (Optional)

1. Go to **Settings → Integrations → PagerDuty**
2. Enter PagerDuty Integration Key
3. Map Sentry projects to PagerDuty services
4. Configure escalation policies

---

## Setting Up Dashboards

### Default Dashboard

Sentry automatically creates dashboards for each project showing:
- Error frequency over time
- Most common errors
- Affected users
- Performance metrics

### Custom Dashboard

Create a unified dashboard for all services:

1. Go to **Dashboards → Create Dashboard**
2. Name it: "ClickEnRent Production Overview"
3. Add widgets:

**Widget 1: Error Count by Service**
- Type: Table
- Query: `event.type:error`
- Group by: `project`

**Widget 2: Error Trends**
- Type: Line Chart
- Query: `event.type:error`
- Group by: `time`

**Widget 3: Top Errors**
- Type: Top Events
- Query: `event.type:error`
- Display: Error title, count, affected users

**Widget 4: Performance by Service**
- Type: Table
- Query: `event.type:transaction`
- Metrics: P50, P95, P99 latency

**Widget 5: Tenant-Specific Errors**
- Type: Table
- Query: `event.type:error has:company_ids`
- Group by: `company_ids`

---

## Team Management

### Adding Team Members

1. Go to **Settings → Teams**
2. Click **Create Team** (e.g., "Backend Team")
3. Add members:
   - **Owner**: Full access to all settings
   - **Manager**: Can manage projects and team members
   - **Admin**: Can configure integrations and alerts
   - **Member**: Can view errors and assign issues

### Access Control

Set project-specific permissions:

1. Go to **Settings → Projects → [Your Project]**
2. Click **Teams**
3. Add teams with appropriate roles:
   - **Backend Team**: Full access to all services
   - **DevOps Team**: View-only access for monitoring
   - **Support Team**: View-only access to error reports

---

## Testing the Integration

### Step 1: Trigger Test Errors

After deploying with Sentry configured:

```bash
# Test auth-service error tracking
curl -X GET http://localhost:8081/api/v1/test/error

# Or trigger an actual error
curl -X GET http://localhost:8081/api/v1/users/99999999
```

### Step 2: Verify in Sentry

1. Go to **Issues** in Sentry dashboard
2. You should see the error appear within seconds
3. Check the error details:
   - **Stack trace**: Full Java stack trace
   - **Tags**: `http_status`, `request_path`, `company_ids`
   - **Context**: Tenant information, environment
   - **Breadcrumbs**: Recent log messages

### Step 3: Test Performance Monitoring

```bash
# Make some API requests
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123"}'
```

1. Go to **Performance** in Sentry dashboard
2. You should see transactions:
   - Transaction name: `POST /api/v1/auth/login`
   - Duration, throughput, error rate
   - Spans for database queries, external API calls

### Step 4: Test OAuth Performance Tracking

```bash
# Trigger Google OAuth flow
curl -X POST http://localhost:8081/api/v1/oauth/google/callback \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code"}'
```

Check Sentry for:
- Transaction: `oauth.login`
- Spans: `oauth.token_exchange`, `oauth.user_info_fetch`

---

## Best Practices

### 1. Error Grouping

Sentry automatically groups similar errors. Review grouping rules:
- **Fingerprint**: Customize how errors are grouped
- **Merge/Split**: Manually adjust grouping if needed

### 2. Release Tracking

Tag errors with releases to track which deployment introduced bugs:

```properties
# Already configured in application.properties
sentry.release=1.0-SNAPSHOT@auth-service
```

Update version when deploying new releases.

### 3. Environment Separation

Use different environments for development, staging, and production:

```bash
# Development
SENTRY_ENVIRONMENT=development

# Staging
SENTRY_ENVIRONMENT=staging

# Production
SENTRY_ENVIRONMENT=production
```

### 4. Sampling Rates

Adjust sampling based on traffic:

```bash
# High traffic: Sample 10% of transactions
SENTRY_TRACES_SAMPLE_RATE=0.1

# Low traffic: Sample 100%
SENTRY_TRACES_SAMPLE_RATE=1.0
```

### 5. Data Scrubbing

Sentry automatically removes sensitive data, but verify:
- Passwords: ✅ Removed
- Credit cards: ✅ Removed
- API keys: ✅ Removed
- User tokens: ✅ Removed (configured with `send-default-pii=false`)

### 6. Issue Workflow

Establish a workflow for handling errors:

1. **Triage**: Review new errors daily
2. **Assign**: Assign to team members
3. **Fix**: Create bug fix
4. **Resolve**: Mark as resolved in Sentry
5. **Monitor**: Track if error recurs

### 7. Performance Budgets

Set performance budgets for critical endpoints:

- **Auth endpoints**: < 200ms P95
- **Search queries**: < 500ms P95
- **Payment processing**: < 1s P95

Alert when budgets are exceeded.

---

## Troubleshooting

### Error: "DSN not configured"

**Cause:** Sentry DSN environment variable not set

**Solution:**
```bash
# Check if DSN is set
echo $SENTRY_DSN_AUTH

# If empty, update .env file
nano .env
```

### Error: "Project not found"

**Cause:** Invalid DSN or project deleted

**Solution:**
1. Verify DSN in Sentry dashboard
2. Regenerate client key if needed
3. Update .env file with correct DSN

### Errors not appearing in Sentry

**Possible causes:**
1. **DSN incorrect**: Verify DSN value
2. **Network blocked**: Check firewall allows HTTPS to `*.ingest.sentry.io`
3. **Rate limited**: Check Sentry quota limits
4. **Disabled**: Verify `sentry.dsn` is set in application.properties

**Debug:**
```bash
# Check Sentry initialization
docker logs auth-service | grep -i sentry

# Test network connectivity
curl https://sentry.io/api/0/
```

### Performance data not captured

**Solution:**
1. Verify `sentry.enable-tracing=true` in application.properties
2. Check `sentry.traces-sample-rate` > 0
3. Ensure Performance Monitoring enabled in Sentry project settings

---

## Cost Optimization

### Free Tier Limits
- 5,000 errors/month
- 10,000 performance units/month

### Optimization Strategies

1. **Sample transactions**: Set `SENTRY_TRACES_SAMPLE_RATE=0.1` (10%)
2. **Filter noisy errors**: Use inbound filters to ignore expected errors
3. **Archive resolved issues**: Reduce storage costs
4. **Use release tracking**: Identify and fix bugs early

### Monitoring Usage

1. Go to **Stats** in Sentry dashboard
2. Track usage by project
3. Set up quota alerts before limits are reached

---

## Support

- **Sentry Documentation**: https://docs.sentry.io/platforms/java/guides/spring-boot/
- **Community Forum**: https://forum.sentry.io/
- **Status Page**: https://status.sentry.io/

---

## Appendix: Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `SENTRY_DSN_AUTH` | Auth service DSN | `https://abc@o123.ingest.sentry.io/456` |
| `SENTRY_DSN_RENTAL` | Rental service DSN | `https://def@o123.ingest.sentry.io/789` |
| `SENTRY_DSN_PAYMENT` | Payment service DSN | `https://ghi@o123.ingest.sentry.io/101` |
| `SENTRY_DSN_SUPPORT` | Support service DSN | `https://jkl@o123.ingest.sentry.io/112` |
| `SENTRY_DSN_NOTIFICATION` | Notification service DSN | `https://mno@o123.ingest.sentry.io/131` |
| `SENTRY_DSN_SEARCH` | Search service DSN | `https://pqr@o123.ingest.sentry.io/415` |
| `SENTRY_DSN_GATEWAY` | Gateway DSN | `https://stu@o123.ingest.sentry.io/161` |
| `SENTRY_ENVIRONMENT` | Environment name | `production`, `staging`, `development` |
| `SENTRY_TRACES_SAMPLE_RATE` | Transaction sampling | `1.0` (100%), `0.1` (10%) |

---

**Setup Complete!** 🎉

Your ClickEnRent microservices are now integrated with Sentry for comprehensive error tracking and performance monitoring.
