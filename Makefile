SHELL    := /bin/bash
GRADLE   ?= ./gradlew
COMPOSE  ?= docker compose

DB_SERVICE ?= postgres
DB_USER    ?= admin
DB_NAME    ?= crud

SEED_TOTAL ?= 1000
TESTS      ?=

.DEFAULT_GOAL := help

.PHONY: help up down reset logs psql run seed seed-fresh compile test test-one clean

help: ## Show this help
	@echo "Usage: make <target>"
	@echo
	@awk 'BEGIN {FS = ":.*?## "}; /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo
	@echo "Overridable variables: SEED_TOTAL (default $(SEED_TOTAL)), TESTS"

up: ## Start Postgres and Adminer in the background
	$(COMPOSE) up -d
	@echo "Postgres on 127.0.0.1:5432, Adminer on http://127.0.0.1:8083"

down: ## Stop the containers, keeping the database volume
	$(COMPOSE) down

reset: ## Stop the containers and DELETE the database volume (destructive)
	@read -p "This deletes the local database volume. Continue? [y/N] " ans; [[ $$ans == [yY] ]] || exit 1; $(COMPOSE) down -v

logs: ## Tail container logs
	$(COMPOSE) logs -f

psql: ## Open a psql shell on the local database
	$(COMPOSE) exec $(DB_SERVICE) psql -U $(DB_USER) -d $(DB_NAME)

run: ## Run the application (Flyway migrations run on startup)
	$(GRADLE) bootRun

seed: ## Insert dummy pegawai via the seeder profile, then exit
	$(GRADLE) bootRun --args='--spring.profiles.active=seeder --spring.main.web-application-type=none --app.seeder.total=$(SEED_TOTAL)'

seed-fresh: ## TRUNCATE pegawai then seed from scratch (destructive)
	@read -p "This deletes every pegawai row. Continue? [y/N] " ans; [[ $$ans == [yY] ]] || exit 1; $(COMPOSE) exec -T $(DB_SERVICE) psql -U $(DB_USER) -d $(DB_NAME) -c "TRUNCATE pegawai RESTART IDENTITY;"
	$(MAKE) seed

compile: ## Compile main sources only (fast sanity check)
	$(GRADLE) compileJava

test: ## Run the full test suite (needs the database up)
	$(GRADLE) test

test-one: ## Run a single test class: make test-one TESTS=com.example.demo.DemoApplicationTests
	@[[ -n "$(TESTS)" ]] || { echo "TESTS is required, e.g. make test-one TESTS=com.example.demo.DemoApplicationTests"; exit 1; }
	$(GRADLE) test --tests '$(TESTS)'

clean: ## Remove build outputs
	$(GRADLE) clean
