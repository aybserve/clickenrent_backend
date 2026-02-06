# IntelliJ IDEA Setup for search-service

## Configure Environment Variables in Run Configuration

Since you're running the service from IntelliJ IDEA, you need to add environment variables to the Run Configuration.

### Step 1: Open Run Configuration

1. Click the **dropdown** next to the Run button (top right)
2. Select **Edit Configurations...**
3. Find **SearchServiceApplication** in the left panel
4. If not there, click **+** → **Application** and configure:
   - Name: `SearchServiceApplication`
   - Main class: `org.clickenrent.searchservice.SearchServiceApplication`
   - Module: `search-service`

### Step 2: Add Environment Variables

In the **Environment variables** field, paste this (all on one line or use the helper):

```
ES_URIS=http://localhost:9200;ES_USERNAME=elastic;ES_PASSWORD=pgyc7efYGXFyRQTL2sCXu2p8M1utG6doqnZVl3y0dEk=;JWT_SECRET=Y2xpY2tlbnJlbnQtc2VjcmV0LWtleS1jaGFuZ2UtaW4tcHJvZHVjdGlvbi0yNTYtYml0
```

**Or use the UI helper:**
1. Click the folder icon (📁) next to "Environment variables"
2. Click **+** to add each variable:
   - `ES_URIS` = `http://localhost:9200`
   - `ES_USERNAME` = `elastic`
   - `ES_PASSWORD` = `pgyc7efYGXFyRQTL2sCXu2p8M1utG6doqnZVl3y0dEk=`
   - `JWT_SECRET` = `Y2xpY2tlbnJlbnQtc2VjcmV0LWtleS1jaGFuZ2UtaW4tcHJvZHVjdGlvbi0yNTYtYml0`
3. Click **OK**

### Step 3: Apply and Run

1. Click **Apply**
2. Click **OK**
3. Run the application

## Alternative: Use .env file plugin

If you want to use the .env file automatically:

1. Install plugin: **File** → **Settings** → **Plugins** → Search for "EnvFile"
2. Install **EnvFile** plugin
3. Restart IntelliJ
4. Go to Run Configuration
5. Check **Enable EnvFile**
6. Add path to `.env` file: `/Users/vitaliyshvetsov/IdeaProjects/backend/.env`

## Verify Configuration

After starting, you should see:
```
✅ Successfully loaded .env file with X properties
Elasticsearch URI: http://localhost:9200
Elasticsearch Username: elastic
Elasticsearch Password: ***SET***
...
✅ Elasticsearch indices verification completed successfully
```

## Troubleshooting

If you still get authentication errors:

1. **Verify Elasticsearch is running:**
   ```bash
   docker ps | grep elasticsearch
   ```

2. **Test authentication from terminal:**
   ```bash
   curl -u elastic:pgyc7efYGXFyRQTL2sCXu2p8M1utG6doqnZVl3y0dEk= http://localhost:9200
   ```

3. **Check environment variables are loaded:**
   Add this to your SearchServiceApplication main method temporarily:
   ```java
   System.out.println("ES_PASSWORD from env: " + System.getenv("ES_PASSWORD"));
   System.out.println("ES_PASSWORD from prop: " + System.getProperty("ES_PASSWORD"));
   ```

4. **Restart IntelliJ** - Sometimes Run Configurations don't update properly
