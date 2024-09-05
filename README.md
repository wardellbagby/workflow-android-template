Workflow - Template Repo
========================

This repo contains all you need to get started with Square's Workflow library for Android
development.

You can [use this link](https://github.com/wardellbagby/workflow-template-app/generate) to generate
your own repo on Github, or manually clone this repo yourself.

## What does this template even do?

This shows three screens: one that uses Jetpack Compose, one that uses regular Android Views, and
one that uses Android ViewBinding. Each screen has a single button that, when clicked, will
transition to the next screen in a loop.

## Things you likely want to read

### Dependencies

Dependencies are handled via a `Dependencies` class in `buildSrc`.

### Build Scripts

All build scripts use Kotlin instead of Groovy for their language.

### Code formatting

This project uses Ktlint to format the Kotlin code. You can manually format the code with Ktlint by
doing:

```shell
./gradlew ktlintFormat
```

To enable the Ktlint Git Hook to check or format your code on commit, be sure to run one of the two
below commands:

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

- Workflow - 1.12.1-beta12
- Kotlin - 2.0.20