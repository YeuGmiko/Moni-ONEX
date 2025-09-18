import { addIcons } from 'oh-vue-icons'
import * as IonIcons from 'oh-vue-icons/icons/io'
import * as PrimeIcons from 'oh-vue-icons/icons/pi'
import * as FontAwesome from 'oh-vue-icons/icons/fa'
import * as Octicons from 'oh-vue-icons/icons/oi'
import * as HeroIcons from 'oh-vue-icons/icons/hi'
import * as LineAwesome from 'oh-vue-icons/icons/la'
import * as RemixIcon from 'oh-vue-icons/icons/ri'
import * as FlatColorIcons from 'oh-vue-icons/icons/fc'



export function setupOhVueIcons() {
    addIcons(...Object.values(IonIcons))
    addIcons(...Object.values(PrimeIcons))
    addIcons(...Object.values(FontAwesome))
    addIcons(...Object.values(Octicons))
    addIcons(...Object.values(HeroIcons))
    addIcons(...Object.values(LineAwesome))
    addIcons(...Object.values(RemixIcon))
    addIcons(...Object.values(FlatColorIcons))
}
