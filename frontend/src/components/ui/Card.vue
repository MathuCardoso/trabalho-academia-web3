<template>
    <article>
        <header class="card-header">
            <slot name="header"> </slot>
        </header>
        <div class="card-body">
            <slot> </slot>
        </div>
        <footer class="card-footer">
            <slot name="footer"> </slot>
        </footer>
    </article>
</template>

<style scoped>
    article {
        position: relative;
        min-width: 150px;
        min-height: 150px;
        padding: 20px;
        background: linear-gradient(120deg, #2a2d3683, #0b0d12d3);
        box-shadow: 2px 2px 8px #0b0d12d3;
        border-radius: 18px;
        transition: all 0.3s ease;
        z-index: 0;
        &:hover {
            box-shadow: 0 0 8px var(--color-pink-accent-shadow);
            z-index: 0;
        }

        .card-header {
            text-align: center;
            font-size: 1.2rem;
        }

        .card-body {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
    }
</style>
