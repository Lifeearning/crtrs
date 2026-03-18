<template>
    <div class="common-layout" style="width: 100%;">
            <el-container>
                <el-header>
                    <Menu />
                </el-header>
                <el-main>
                    <router-view></router-view>
                </el-main>
            </el-container>
            <el-backtop :right="100" :bottom="100" />
    </div>
</template>

<script setup>
import Menu from '@/components/menu/index.vue'
</script>

<style scoped>
</style>