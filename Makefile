all: format-check build

git-setup: git-template git-hooks
	@echo "✅ Done. (repo-local git template + hooks applied)"

git-template:
	@echo "Setting git commit template..."
	@git config commit.template .gitmessage.txt
	@echo "Done."

git-hooks:
	@echo "Enabling repo hooks (.githooks)..."
	@git config core.hooksPath .githooks
	@chmod +x .githooks/commit-msg
	@echo "Done. (commit-msg hook active)"

# Code quality and formatting
format-check:
	@echo "Checking code formatting..."
	@./gradlew spotlessCheck

format-apply:
	@echo "Applying code formatting..."
	@./gradlew spotlessApply

build:
	@echo "Building project (includes format check)..."
	@./gradlew build
