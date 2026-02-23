# CI/CD Documentation

This document describes the Continuous Integration and Continuous Deployment pipelines for the Task Tracker Backend.

## 📋 Table of Contents

- [Overview](#overview)
- [Workflows](#workflows)
- [Setup](#setup)
- [Usage](#usage)
- [Secrets Configuration](#secrets-configuration)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

The project uses **GitHub Actions** for CI/CD automation. The pipelines handle:

- ✅ Building and testing the application
- ✅ Code quality checks
- ✅ Security scanning
- ✅ Docker image building
- ✅ Pull request validation
- ✅ Automated deployments
- ✅ Release creation

## 🔄 Workflows

### 1. CI Pipeline (`ci.yml`)

**Trigger:** Push or Pull Request to `main`, `master`, or `develop` branches

**Jobs:**

#### a. Build and Test
- Sets up MySQL 8.0 as service container
- Compiles Kotlin code with Gradle
- Runs unit and integration tests
- Generates test reports and coverage
- Uploads test results as artifacts
- Creates application JAR file

**Environment:**
- JDK 17 (Temurin distribution)
- MySQL 8.0 (test database)
- Gradle cache enabled

#### b. Code Quality Check
- Runs Kotlin linter
- Checks code formatting
- Validates Gradle configuration
- Ensures code standards compliance

#### c. Docker Build
- Builds Docker image using multi-stage Dockerfile
- Uses layer caching for faster builds
- Tests Docker image by running container
- Validates health check endpoint

#### d. Security Scan
- OWASP dependency vulnerability check
- Identifies security vulnerabilities in dependencies
- Generates security report

#### e. Status Check
- Aggregates results from all jobs
- Provides final pass/fail status
- Fails if any critical job fails

**Status Badge:**
```markdown
![Backend CI](https://github.com/<username>/<repo>/workflows/Backend%20CI/badge.svg)
```

### 2. CD Pipeline (`cd.yml`)

**Trigger:**
- Push to `main` or `master` branch
- Git tags matching `v*` pattern
- Manual dispatch

**Jobs:**

#### a. Deploy Application
- Builds production-ready JAR
- Extracts version from Gradle
- Creates release artifact (JAR + configs)
- Uploads artifacts

**Environments:**
- Staging (default)
- Production (manual trigger)

#### b. Docker Publish
- Builds Docker image
- Tags with semantic versioning
- Pushes to Docker Hub (when configured)
- Uses Docker layer caching

#### c. Create Release
- Creates GitHub release for version tags
- Attaches release artifacts
- Generates release notes automatically

#### d. Notification
- Reports deployment status
- Can integrate with Slack/Discord
- Provides deployment summary

**Manual Deployment:**
Go to Actions → Backend CD → Run workflow → Select environment

### 3. PR Quality Check (`pr-check.yml`)

**Trigger:** Pull request events (opened, synchronized, reopened)

**Jobs:**

#### a. PR Information
- Displays PR metadata
- Shows author, branches, and title

#### b. Validate PR
- Checks PR title follows conventional commits
- Verifies no large files (>5MB)
- Validates commit message format
- Enforces PR standards

**Conventional Commit Format:**
```
type(scope): description

Types: feat, fix, docs, style, refactor, test, chore, perf
```

Examples:
- `feat(api): add task filtering endpoint`
- `fix(auth): resolve JWT token expiration`
- `docs: update API documentation`

#### c. Build Check
- Compiles code
- Runs all tests
- Validates build succeeds

#### d. Code Review
- Automated linting
- Checks for TODO/FIXME comments
- Detects debug println statements
- Suggests improvements

#### e. Changes Summary
- Lists changed files
- Shows statistics (additions/deletions)
- Categorizes changes

#### f. PR Labeler
- Auto-labels based on changed files
- Labels: documentation, api, database, security, tests, etc.

#### g. Final Check
- Validates all checks passed
- Comments on PR when ready
- Fails if critical issues found

## ⚙️ Setup

### 1. Enable GitHub Actions

Actions are automatically enabled for public repositories. For private repos:

1. Go to repository Settings
2. Navigate to Actions → General
3. Enable "Allow all actions and reusable workflows"

### 2. Required Branch Protection

Configure branch protection for `main`/`master`:

1. Settings → Branches → Add rule
2. Branch name pattern: `main`
3. Enable:
   - ✅ Require pull request reviews
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Require conversation resolution

4. Status checks to require:
   - Build and Test
   - Code Quality Check
   - Docker Build

### 3. Secrets Configuration

Required for full CD functionality:

#### Repository Secrets

Settings → Secrets and variables → Actions → New repository secret

| Secret Name | Description | Required For |
|------------|-------------|--------------|
| `DOCKER_USERNAME` | Docker Hub username | Docker image push |
| `DOCKER_PASSWORD` | Docker Hub password | Docker image push |
| `SLACK_WEBHOOK` | Slack webhook URL | Notifications |

#### Environment Secrets

For environment-specific deployments:

**Staging Environment:**
- `STAGING_DB_URL`
- `STAGING_DB_USER`
- `STAGING_DB_PASS`

**Production Environment:**
- `PROD_DB_URL`
- `PROD_DB_USER`
- `PROD_DB_PASS`

### 4. Configure Environments

Settings → Environments → New environment

**Staging:**
- No protection rules
- Auto-deploy on push to develop

**Production:**
- Required reviewers: 1-2 people
- Deployment delay: Optional
- Manual approval required

## 🚀 Usage

### Running CI Manually

1. Go to Actions tab
2. Select "Backend CI"
3. Click "Run workflow"
4. Select branch
5. Click "Run workflow"

### Creating a Deployment

**Option 1: Automatic (Push to main)**
```bash
git checkout main
git merge develop
git push origin main
# CD workflow triggers automatically
```

**Option 2: Manual Dispatch**
1. Go to Actions → Backend CD
2. Click "Run workflow"
3. Select branch
4. Choose environment (staging/production)
5. Click "Run workflow"

**Option 3: Version Tag**
```bash
# Create and push version tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
# Creates release and deploys
```

### Creating a Pull Request

1. **Create feature branch:**
   ```bash
   git checkout -b feat/new-feature
   ```

2. **Make changes and commit:**
   ```bash
   git add .
   git commit -m "feat(api): add new endpoint"
   ```

3. **Push and create PR:**
   ```bash
   git push origin feat/new-feature
   ```

4. **PR checks run automatically:**
   - Title validation
   - Build check
   - Code review
   - Security scan

5. **Address any failures:**
   - Fix issues locally
   - Push changes
   - Checks re-run automatically

6. **Merge when checks pass:**
   - All checks green ✅
   - Reviews approved
   - Merge to main

### Viewing Build Status

**In Pull Requests:**
- Status checks shown at bottom
- Click "Details" to view logs
- Artifacts available for download

**In Actions Tab:**
- View all workflow runs
- Filter by branch, workflow, status
- Download artifacts
- Re-run failed jobs

### Downloading Artifacts

1. Go to Actions
2. Click on workflow run
3. Scroll to "Artifacts" section
4. Download:
   - `application-jar` - Built JAR file
   - `test-results` - Test reports
   - `coverage-report` - Code coverage
   - `security-report` - Vulnerability scan

## 🔐 Secrets Configuration

### Docker Hub Publishing

To enable Docker image publishing:

1. **Create Docker Hub account** at hub.docker.com

2. **Add secrets to GitHub:**
   ```
   DOCKER_USERNAME: your-dockerhub-username
   DOCKER_PASSWORD: your-dockerhub-password-or-token
   ```

3. **Update cd.yml:**
   ```yaml
   - name: Log in to Docker Hub
     uses: docker/login-action@v3
     with:
       username: ${{ secrets.DOCKER_USERNAME }}
       password: ${{ secrets.DOCKER_PASSWORD }}

   # Change push: false to push: true
   push: true
   ```

### Slack Notifications

To enable Slack notifications:

1. **Create Slack Webhook:**
   - Go to Slack App settings
   - Create Incoming Webhook
   - Copy webhook URL

2. **Add secret:**
   ```
   SLACK_WEBHOOK: your-webhook-url
   ```

3. **Uncomment in workflows:**
   ```yaml
   - name: Send Slack notification
     uses: 8398a7/action-slack@v3
     with:
       status: ${{ job.status }}
       webhook_url: ${{ secrets.SLACK_WEBHOOK }}
   ```

## 🐛 Troubleshooting

### Build Failures

**Problem:** Tests fail in CI but pass locally

**Solutions:**
1. Check MySQL service is running
2. Verify environment variables are set
3. Ensure test database is clean
4. Check for timezone issues
5. Review test logs in artifacts

**Problem:** Gradle build timeout

**Solutions:**
1. Increase timeout in workflow
2. Enable Gradle daemon
3. Use dependency caching
4. Check network connectivity

### Docker Build Issues

**Problem:** Docker build fails with OOM

**Solutions:**
1. Reduce parallel builds
2. Use multi-stage build
3. Clear Docker cache
4. Increase runner resources

**Problem:** Docker image won't start

**Solutions:**
1. Check environment variables
2. Verify database connectivity
3. Review application logs
4. Test locally with same env

### PR Check Failures

**Problem:** Commit message validation fails

**Solution:**
Use conventional commit format:
```bash
git commit -m "feat: add new feature"
# OR
git commit --amend
# Edit message to follow format
```

**Problem:** Large files detected

**Solution:**
```bash
# Remove large files
git rm large-file.zip

# Use Git LFS for large files
git lfs track "*.zip"
git add .gitattributes
```

### Workflow Permission Issues

**Problem:** Workflow can't create comments/labels

**Solution:**
1. Go to Settings → Actions → General
2. Workflow permissions → Read and write permissions
3. Save changes

### Cache Issues

**Problem:** Stale cache causing failures

**Solution:**
1. Go to Actions → Caches
2. Delete relevant caches
3. Re-run workflow

## 📊 Monitoring

### Key Metrics

Monitor these metrics for CI/CD health:

- **Build Success Rate**: Target > 95%
- **Average Build Time**: Target < 10 minutes
- **Test Coverage**: Target > 80%
- **Security Vulnerabilities**: Target = 0 critical

### GitHub Insights

View repository insights:
- Actions usage (minutes)
- Workflow runs over time
- Success/failure trends
- Most failing workflows

## 🔄 Workflow Maintenance

### Updating Dependencies

Keep Actions updated:

```bash
# Check for updates
gh extension install actions/gh-actions-cache
gh actions-cache list

# Update action versions in workflows
# Example: uses: actions/checkout@v3 → uses: actions/checkout@v4
```

### Performance Optimization

1. **Enable caching:**
   - Gradle dependencies
   - Docker layers
   - Test results

2. **Parallelize jobs:**
   - Run independent jobs concurrently
   - Split test suites

3. **Skip unnecessary steps:**
   - Skip tests on docs-only changes
   - Conditional job execution

## 📚 References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Build Action](https://github.com/gradle/gradle-build-action)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [Conventional Commits](https://www.conventionalcommits.org/)

## 🤝 Contributing to CI/CD

To improve the CI/CD pipelines:

1. Test changes in a fork first
2. Document new workflows
3. Update this documentation
4. Get review from team
5. Monitor after merge

---

**Questions?** Create an issue with the `ci-cd` label.
