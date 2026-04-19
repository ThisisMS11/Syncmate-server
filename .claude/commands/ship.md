Stage all changes, commit, push to origin master, then trigger the GCP Cloud Build.

## Steps

1. Run `git status` and `git diff` to see what has changed.
2. Run `git add -A` to stage all changes.
3. Generate a concise conventional-commit message that accurately reflects the diff (prefix: feat/fix/refactor/chore as appropriate). Do NOT ask the user for a message — derive it from the diff.
4. Commit using a heredoc so formatting is safe:
   ```
   git commit -m "$(cat <<'EOF'
   <your message here>

   Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
   EOF
   )"
   ```
5. Run `git push origin master`.
6. Trigger the Cloud Build:
   ```
   gcloud builds triggers run ed19a902-c468-4e9e-adee-5412b2bb8089 \
     --branch=master \
     --project=macro-precinct-458804-t0
   ```
7. Report the Cloud Build URL (from the trigger run output) so the user can watch progress.

## Constraints

- Never use `--no-verify`.
- Never force-push.
- If `git status` shows nothing to commit, skip steps 2-5 and go straight to triggering the build.
- If the commit or push fails, stop and report the error — do not trigger the build.
