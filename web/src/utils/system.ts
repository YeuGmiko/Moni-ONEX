export async function copyText(text: string) {
    try {
        await navigator.clipboard.writeText(text)
        window?.$message?.success(`已复制文本：${text}`)
    } catch(e) {
        console.error(`文本复制失败，error：${e}`)
        window?.$message?.error(`复制文本失败：${text}`)
    }
}
export function checkImageExists(url: string) {
    return new Promise(resolve => {
        const img = new Image()
        img.onload = function() { resolve(true) }
        img.onerror = function() { resolve(false) }
        img.src = url
    })
}

