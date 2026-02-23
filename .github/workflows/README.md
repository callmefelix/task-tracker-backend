# GitHub Actions Workflows

This directory contains CI/CD workflows for the Task Tracker Backend.

## 📁 Workflow Files

### `ci.yml` - Continuous Integration
**Purpose:** Build, test, and validate code on every push/PR

**Triggers:**
- Push to `main`, `master`, `develop`
- Pull requests to these branches

**What it does:**
- ✅ Builds application with Gradle
- ✅ Runs tests with MySQL database
- ✅ Performs code quality checks
- ✅ Builds Docker image
- ✅ Runs security scans
- ✅ Uploads artifacts (JAR, reports)

**Duration:** ~5-10 minutes

---

### `cd.yml` - Continuous Deployment
**Purpose:** Deploy application to environments

**Triggers:**
- Push to `main` or `master`
- Version tags (v*)
- Manual workflow dispatch

**What it does:**
- ✅ Builds production JAR
- ✅ Creates release artifacts
- ✅ Builds and publishes Docker image
- ✅ Creates GitHub releases
- ✅ Sends notifications

**Environments:**
- Staging (auto-deploy)
- Production (manual approval)

**Duration:** ~8-12 minutes

---

### `pr-check.yml` - Pull Request Quality
**Purpose:** Validate PR quality before merge

**Triggers:**
- Pull request opened/updated

**What it does:**
- ✅ Validates PR title format
- ✅ Checks commit messages
- ✅ Runs build and tests
- ✅ Performs code review checks
- ✅ Auto-labels PR
- ✅ Comments when ready

**Duration:** ~5-8 minutes

---

### `labeler.yml` - Auto Labeling
**Purpose:** Configuration for auto-labeling PRs

**Labels PRs based on:**
- Changed file paths
- File types
- Branch names

---

## 🚀 Quick Actions

### Run CI Manually
```
Actions → Backend CI → Run workflow
```

### Deploy to Staging
```
Actions → Backend CD → Run workflow → Select "staging"
```

### Deploy to Production
```
Actions → Backend CD → Run workflow → Select "production"
```

### Create Release
```
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

## 📊 Status Badges

Add to README.md:

```markdown
![CI](https://github.com/<owner>/<repo>/workflows/Backend%20CI/badge.svg)
![CD](https://github.com/<owner>/<repo>/workflows/Backend%20CD/badge.svg)
```

## 🔧 Configuration

See [CI_CD.md](../../CI_CD.md) for detailed configuration instructions.

## 📈 Monitoring

View workflow runs:
- Actions tab in repository
- Filter by workflow, branch, status
- Download artifacts and logs

## ❓ Help

For issues or questions:
- Review [CI_CD.md](../../CI_CD.md)
- Check workflow logs
- Create issue with `ci-cd` label
