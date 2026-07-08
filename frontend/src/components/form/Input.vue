<script setup>
    import { Eye, EyeOff } from "@lucide/vue";
    import { vMaska } from "maska/vue";
    import { computed, ref } from "vue";
    import Errors from "./Errors.vue";

    const props = defineProps({
        type: {
            type: String,
            default: "text",
        },
        model: String,
        placeholder: String,
        name: String,
        label: String,
        width: {
            type: String,
            default: "100%",
        },
        mask: String | Array,
        maskTokens: String,
        error: String,
    });

    const emit = defineEmits(["updateValue"]);
    const mostrarSenha = ref(false);
    const tipoDoInput = computed(() => {
        if (props.type !== "password") return props.type;
        return mostrarSenha.value ? "text" : "password";
    });
</script>

<template>
    <div class="input-group">
        <label for="props.name">{{ props.label }}</label>
        <div class="input-wrapper">
            <input
                :class="{ 'has-password-toggle': props.type === 'password' }"
                :value="props.model"
                :name="props.name"
                :type="tipoDoInput"
                :placeholder="props.placeholder"
                v-maska
                :data-maska="props.mask"
                :data-maska-tokens="props.maskTokens"
                @input="emit('updateValue', $event.target.value)"
            />
            <button
                v-if="props.type === 'password'"
                type="button"
                class="password-toggle"
                :title="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'"
                @click="mostrarSenha = !mostrarSenha"
            >
                <EyeOff v-if="mostrarSenha" :size="19" />
                <Eye v-else :size="19" />
            </button>
        </div>
        <Errors :error="props.error" />
    </div>
</template>

<style scoped>
    .input-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
        width: 100%;
        label {
            color: white;
        }
        .input-wrapper {
            position: relative;
            width: v-bind("props.width");
        }
        input {
            color: white;
            border: 2px solid var(--color-pink-accent);
            border-radius: var(--radius-input);
            padding: 8px;
            transition: all 0.2s ease;
            width: 100%;
            &::placeholder {
                font-size: 0.9rem;
            }
            &:hover {
                box-shadow: 2px 2px 10px var(--color-pink-accent-shadow);
            }
        }
        input.has-password-toggle {
            padding-right: 42px;
        }
        .password-toggle {
            position: absolute;
            top: 50%;
            right: 10px;
            display: flex;
            color: var(--color-pink-smooth);
            cursor: pointer;
            transform: translateY(-50%);
        }
    }
</style>
