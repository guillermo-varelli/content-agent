# Content Agent — Project Tracker

## What is this?

Spring Boot service that runs AI-powered **content generation workflows**. It scrapes news sources, passes articles through configurable multi-step pipelines, calls LLM providers (Groq, Gemini), and persists generated content to MySQL.

---

## Architecture

```
Scheduler (ContentStatusTask)
    └── WorkflowExecutionService.executeWorkflow()
            ├── iterates Steps ordered by orderIndex
            ├── resolves step type:
            │     ├── "internal"  → InternalOperationService (parses + saves Content)
            │     └── ai step     → IAClientFactory → IAClientStrategy → LLM provider
            └── tracks everything in Execution + StepExecution tables
```

### Core entities

| Entity | Table | Notes |
|---|---|---|
| `Workflow` | `workflows` | has category, sub_category, enabled flag |
| `Step` | `steps` | ordered, has operationType + prompt template + agent ref |
| `Agent` | `agents` | holds provider name + API secret |
| `Execution` | `executions` | one per workflow run, tracks status |
| `StepExecution` | `step_executions` | one per step per run, stores LLM output |
| `Content` | `content` | final parsed content saved by the `internal` step |

### LLM Providers

| Provider | Config class | Notes |
|---|---|---|
| Groq | `GroqProperties` / `GroqService` | OpenAI-compatible endpoint |
| Gemini | `GeminiProperties` / `GeminiService` | Google Generative Language API |

Step prompts use `{{previous_output}}` as a placeholder to chain step outputs.

---

## DB

- MySQL at `localhost:3306/ndb`
- user: `nuser` / pass: `npass`
- Port: `8090`
- DDL in `src/main/resources/struct.sql`

---

## Git history — what was built

| Commit | What changed |
|---|---|
| `add slug` | Added slug field to content |
| `update content add subcategory` | Added `sub_category` to `content` + `workflows` tables |
| `add workflows exec` | Exposed workflow execution endpoint |
| `fix workflows at with same runsteps` | Fixed timing/status bug in execution tracking |
| `add scrape with steps` | Integrated Infobae scraper as a step input source |
| `set error on exec when execution gets error` | Proper ERROR propagation in execution flow |
| `add improve content` | Content generation improvements |
| `add imagen gen step isolated` | Image generation step (isolated, likely via Unsplash) |
| `improve steps` | Step processing refactor |
| `remove backoffice` | Removed NBackOffice module |

---

## Known TODOs / areas to revisit

- `ContentStatusTask.createContentWithAIHumanHacks()` has a broken guard: `ENABLED_WORKFLOW.equals(5)` compares a `List` to an `Integer` — always false. Should be `workflowId.equals(5)` or removed.
- `InfobaeTecnoService.scrapeArticle()` builds the return string but **never includes the article body** (`contenido` is built but not appended).
- `WorkflowExecutionService`: execution timestamps (`createdAt`) are overwritten mid-loop instead of only on creation.
- Unsplash image download logic is commented out in scheduler — may need to be re-wired.
- `InternalOperationService` returns a `Map` that is then `.toString()`'d — next step gets ugly serialized text.

---

## Scheduler

`ContentStatusTask` runs every **1 second** (`fixedDelay = 1000`), fetches enabled workflows and fires `executeWorkflow` for each. The Infobae scrape scheduler is currently commented out.

---

## Running locally

```bash
# Start MySQL (docker-compose)
docker-compose up -d

# Run app
./mvnw spring-boot:run
```
