# Lua Bytecode Deobfuscation (Prototype)

This repository contains a prototype Lua bytecode deobfuscation toolkit focused on control-flow cleanup at the Lua ASM text level. It is intended for study and reference only and is not a turn‑key tool.

## Core idea (high level)
- Parse a textual Lua ASM listing into instructions and build basic blocks.
- Track control-flow edges (direct jumps and conditional branches) and identify “garbage” or invalid blocks.
- Perform small, targeted deobfuscations:
  - Normalize invalid/unused jumps and tailcalls.
  - Rewrite specific patterns around tforloop/forprep when used as obfuscation.
  - Inline trivial jump chains and remove intermediary single‑jmp blocks.
  - Diffuse through garbage blocks and refine the CFG to simplify successors.
- Provide lightweight dataflow helpers to find register/opcode uses that guide the above transformations.

Key components:
- Instruction: opcode/operand parsing helpers and simple pattern checks.
- BasicBlock/LuaFunction: in-memory CFG representation.
- DataFlowAnalysis: index uses of registers/opcodes per block.
- LuaASMParser: parsing, CFG construction, deobfuscation passes, and printing.

## Important limitations
- Prototype quality: highly tailored to a specific Lua ASM dump format; not a general deobfuscator.
- Incomplete opcode semantics: only a subset of instructions are handled for control-flow decisions.
- Heuristic transformations: patterns may be misidentified on unseen inputs.
- No build/run setup: there is no ready-to-run CLI or app; glue code and input producers are not included.

Because of the above, this project is for reference and learning only. It cannot be directly executed as-is and will require substantial adaptation to your environment, ASM format, and use case.

## Status
Experimental, incomplete, and unmaintained. Use at your own risk.
