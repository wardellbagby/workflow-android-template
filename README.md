Workflow - Template Repo
========================

This repo contains all you need to get started with Square's Workflow library for Android
development.

You can [use this link](https://github.com/wardellbagby/workflow-template-app/generate) to generate your own repo on Github, or manually clone this repo yourself.

## Things you likely want to read

To enable the Ktlint Git Hook to check or format your code on commit, be sure to run one of the two below commands:
```shell
# Formats code on commit, failing commit if code cannot be formatted.
./gradlew addKtlintFormatGitPreCommitHook
```

```shell
# Checks code on commit, failing commit if code fails Ktlint check.
./gradlew addKtlintCheckGitPreCommitHook
```

You may also want to run the follow to get IDEA (or Android Studio) to use the Ktlint code style:

```
# Generates IDEA / Android Studio code styles in the current folder's .idea subdirectory.
./gradlew ktlintApplyToIdea
```

## Project Dependencies

- Workflow - 1.7.0 (with Compose support)
- Kotlin - 1.6.10
- Jetpack Compose (with Material Design) - 1.1.1
- ConstraintLayout - 2.1.3
- Cycler - 0.1.4
- Material Design - 1.5.0