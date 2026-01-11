BUILD_NUM=$$(cat ./build_num.txt)
BUILD_NUM_FILE=./build_num.txt
VERSION_NUM=$$(cat ./version.txt)

TARGET_VERSION=$(VERSION_NUM).$(BUILD_NUM)

MODULE_NAME=live-platform-lab
DOCKER_REPOSITORY=kjuiop

all: build

build:
	@echo "Building project (includes format check)..."
	@./gradlew build

config:
	@if [ ! -d $(TARGET_DIR) ]; then mkdir $(TARGET_DIR); fi
	@if [ ! -d $(TARGET_BACKUP_DIR) ]; then mkdir $(TARGET_BACKUP_DIR); fi

build_num:
	@echo $$(($$(cat $(BUILD_NUM_FILE)) + 1 )) > $(BUILD_NUM_FILE)

docker_build:
	docker build --platform linux/amd64 --tag $(DOCKER_REPOSITORY)/$(MODULE_NAME):$(VERSION_NUM).$(BUILD_NUM) .

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

target-version:
	@echo "========================================"
	@echo "APP_VERSION    : $(VERSION_NUM)"
	@echo "BUILD_NUM      : $(BUILD_NUM)"
	@echo "TARGET_VERSION : $(TARGET_VERSION)"
	@echo "========================================"